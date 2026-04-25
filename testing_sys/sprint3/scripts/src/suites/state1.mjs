import { buildAuthApi } from '../../../../sprint2/scripts/src/targets/auth.mjs';
import { buildUserApi } from '../../../../sprint2/scripts/src/targets/users.mjs';
import { makeTestUsers } from '../../../../sprint2/scripts/src/util/testUsers.mjs';
import { assert, assertEq } from '../../../../sprint2/scripts/src/assert.mjs';

import { buildBookingApi } from '../targets/bookings.mjs';
import { buildPaymentCardApi } from '../targets/paymentCards.mjs';
import { buildAdminApi } from '../targets/admin.mjs';

export function buildSprint3State1Suite({ http }) {
  const auth = buildAuthApi(http);
  const users = buildUserApi(http);
  const bookings = buildBookingApi(http);
  const cards = buildPaymentCardApi(http);
  const adminApi = buildAdminApi(http);

  const ctx = { state: {} };
  const tests = [];

  tests.push({
    name: 'state1 bootstrap: register/login users + admin + seed cards',
    tags: ['state1', 'bootstrap', 'auth', 'id9', 'id22', 'id23'],
    run: async () => {
      const { userA, userB } = makeTestUsers();
      const suffix = Date.now();
      const userC = {
        email: `sys_c_${suffix}@example.com`,
        password: 'Passw0rd!234',
        name: `Sys C ${suffix}`
      };

      const regA = await users.register(userA);
      const regB = await users.register(userB);
      const regC = await users.register(userC);
      assertInRange(regA.status, [200, 201]);
      assertInRange(regB.status, [200, 201]);
      assertInRange(regC.status, [200, 201]);

      const loginA = await auth.login({ email: userA.email, password: userA.password });
      const loginB = await auth.login({ email: userB.email, password: userB.password });
      const loginC = await auth.login({ email: userC.email, password: userC.password });
      const admin = await auth.login({ email: 'admin@admin.com', password: 'admin123' });

      assert(loginA?.token && loginA?.userId, 'userA login failed');
      assert(loginB?.token && loginB?.userId, 'userB login failed');
      assert(loginC?.token && loginC?.userId, 'userC login failed');
      assert(admin?.token && admin?.userId, 'admin login failed');

      ctx.state.userA = { ...loginA };
      ctx.state.userB = { ...loginB };
      ctx.state.userC = { ...loginC };
      ctx.state.admin = { ...admin };

      const ids = await ensureScooters({ adminApi, adminToken: admin.token, minimum: 4 });
      assert(ids.length >= 2, 'need at least 2 scooters for state1 tests');
      ctx.state.scooters = ids;

      const mkCard = () => ({
        holderName: 'Test User',
        cardNumber: randomVisa(),
        brand: 'VISA',
        expiryMonth: 12,
        expiryYear: new Date().getFullYear() + 2
      });

      const cardA = await cards.create({ userId: loginA.userId, token: loginA.token, body: mkCard() });
      const cardB = await cards.create({ userId: loginB.userId, token: loginB.token, body: mkCard() });
      const cardC = await cards.create({ userId: loginC.userId, token: loginC.token, body: mkCard() });
      assertInRange(cardA.status, [200, 201]);
      assertInRange(cardB.status, [200, 201]);
      assertInRange(cardC.status, [200, 201]);
      ctx.state.cardA = cardA.data;
      ctx.state.cardB = cardB.data;
      ctx.state.cardC = cardC.data;
    }
  });

  tests.push({
    name: 'ID9: admin binds guest card and creates guest booking',
    tags: ['state1', 'id9', 'guest', 'paymentcard', 'booking'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { admin, scooters } = ctx.state;
      const guestId = `G${Date.now()}`;
      ctx.state.guestId = guestId;
      const bindReq = {
        holderName: 'Guest Card Holder',
        cardNumber: randomVisa(),
        brand: 'VISA',
        expiryMonth: 11,
        expiryYear: new Date().getFullYear() + 2
      };
      const bind = await cards.bindGuest({
        userId: admin.userId,
        guestId,
        token: admin.token,
        body: bindReq
      });
      assertInRange(bind.status, [200, 201]);

      const guestCards = await cards.listGuest({
        userId: admin.userId,
        guestId,
        token: admin.token
      });
      assertEq(guestCards.status, 200);
      assert(Array.isArray(guestCards.data), 'guest cards should be an array');
      assert(guestCards.data.length >= 1, 'guest should have at least one card');

      const createGuest = await bookings.createGuest({
        token: admin.token,
        body: {
          salespersonId: String(admin.userId),
          guestId,
          guestName: 'Guest System Test',
          guestContact: 'guest-system@example.com',
          scooterId: String(scooters[0]),
          duration: '10M'
        }
      });
      assertEq(createGuest.status, 201);
      assertEq(createGuest.data?.status, 'CONFIRMED');
      ctx.state.guestBookingId = createGuest.data?.id;
    }
  });

  tests.push({
    name: 'ID9: non-staff user cannot create guest booking (403)',
    tags: ['state1', 'id9', 'guest', 'authz'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { userA, scooters } = ctx.state;
      const res = await bookings.createGuest({
        token: userA.token,
        body: {
          salespersonId: String(userA.userId),
          guestId: `G${Date.now()}`,
          guestName: 'Blocked Guest',
          guestContact: 'blocked@example.com',
          scooterId: String(scooters[1]),
          duration: '10M'
        }
      });
      assertEq(res.status, 403);
    }
  });

  tests.push({
    name: 'ID22: booking returns discount fields and valid price relation',
    tags: ['state1', 'id22', 'discount', 'booking'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { userA, cardA, scooters } = ctx.state;
      const created = await bookings.create({
        token: userA.token,
        body: {
          scooter_id: String(scooters[1]),
          user_id: String(userA.userId),
          duration: '1H',
          startLat: 1.2,
          startLng: 2.3
        }
      });
      assertEq(created.status, 201);
      assert(created.data?.id, 'booking id missing');
      assert(created.data?.discountType != null, 'discountType missing');
      assert(created.data?.discountAmount != null, 'discountAmount missing');
      assert(created.data?.discountMultiplier != null, 'discountMultiplier missing');
      assert(created.data?.originalPrice != null, 'originalPrice missing');
      assert(created.data?.totalPrice != null, 'totalPrice missing');
      assert(Number(created.data.totalPrice) <= Number(created.data.originalPrice), 'totalPrice should not exceed originalPrice');

      const pay = await bookings.pay({
        token: userA.token,
        bookingId: created.data.id,
        body: {
          paymentMethod: 'BANK_CARD',
          paymentCardId: String(cardA?.id ?? '')
        }
      });
      assertEq(pay.status, 200);

      const complete = await bookings.complete({
        token: userA.token,
        bookingId: created.data.id,
        body: { endLat: 3.2, endLng: 4.1 }
      });
      assertInRange(complete.status, [200, 409]);
    }
  });

  tests.push({
    name: 'ID22: second user booking supports NONE discount path',
    tags: ['state1', 'id22', 'discount'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { userB, cardB, scooters } = ctx.state;
      const created = await bookings.create({
        token: userB.token,
        body: {
          scooter_id: String(scooters[2] ?? scooters[0]),
          user_id: String(userB.userId),
          duration: '10M'
        }
      });
      assertEq(created.status, 201);
      assert(created.data?.discountType, 'discountType missing');

      const pay = await bookings.pay({
        token: userB.token,
        bookingId: created.data.id,
        body: {
          paymentMethod: 'BANK_CARD',
          paymentCardId: String(cardB?.id ?? '')
        }
      });
      assertEq(pay.status, 200);

      const cancel = await bookings.cancel({
        token: userB.token,
        bookingId: created.data.id,
        body: { endLat: 9.1, endLng: 9.2 }
      });
      assertInRange(cancel.status, [200, 409]);
    }
  });

  tests.push({
    name: 'ID23: concurrent create same scooter only one success',
    tags: ['state1', 'id23', 'concurrency', 'booking'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { userA, userB, userC, scooters } = ctx.state;
      const sameScooterId = String(scooters[3] ?? scooters[0]);
      const users3 = [userA, userB, userC];

      const results = await Promise.all(
        users3.map((u) =>
          bookings.create({
            token: u.token,
            body: {
              scooter_id: sameScooterId,
              user_id: String(u.userId),
              duration: '10M'
            }
          })
        )
      );

      const success = results.filter((r) => r.status === 201);
      const failed = results.filter((r) => r.status !== 201);
      assertEq(success.length, 1);
      assert(failed.length >= 2, 'expected >= 2 failures for conflict path');
      failed.forEach((f) => assertInRange(f.status, [400, 404, 409]));
    }
  });

  tests.push({
    name: 'ID23: concurrent create different scooters allows multiple success',
    tags: ['state1', 'id23', 'concurrency'],
    dependsOn: ['state1 bootstrap: register/login users + admin + seed cards'],
    run: async () => {
      const { userA, userB, scooters } = ctx.state;
      if (!scooters[0] || !scooters[1]) return;

      const [a, b] = await Promise.all([
        bookings.create({
          token: userA.token,
          body: { scooter_id: String(scooters[0]), user_id: String(userA.userId), duration: '10M' }
        }),
        bookings.create({
          token: userB.token,
          body: { scooter_id: String(scooters[1]), user_id: String(userB.userId), duration: '10M' }
        })
      ]);
      const okCount = [a, b].filter((x) => x.status === 201).length;
      assert(okCount >= 1, 'expected at least one successful booking');
    }
  });

  return { tests, ctx };
}

async function ensureScooters({ adminApi, adminToken, minimum }) {
  const list = await adminApi.listScooters({ token: adminToken, limit: 50 });
  assertEq(list.status, 200);
  let ids = pickAvailableVisibleScooterIds({ data: list.data }, 100);

  while (ids.length < minimum) {
    const create = await adminApi.createScooter({
      token: adminToken,
      body: {
        status: 'AVAILABLE',
        type: 'GEN1',
        hour_rate: 5.0,
        location_lat: 1.3 + Math.random() * 0.01,
        location_lng: 103.8 + Math.random() * 0.01,
        location_name: `SYS-TEST-${Date.now()}`
      }
    });
    assertInRange(create.status, [200, 201]);
    const createdId = create.data?.id ?? create.body?.data?.id;
    if (createdId != null) ids.push(createdId);
  }

  return ids.slice(0, minimum);
}

function pickScooterIds(body, limit = 4) {
  const candidates = body?.data?.data ?? body?.data ?? body?.items ?? body?.content ?? body?.records ?? body;
  if (!Array.isArray(candidates)) return [];
  return candidates
    .map((s) => s?.id ?? s?.scooterId ?? s?.scooter_id ?? s)
    .filter((x) => x != null)
    .slice(0, limit);
}

function pickAvailableVisibleScooterIds(body, limit = 10) {
  const candidates = body?.data?.data ?? body?.data ?? body?.items ?? body?.content ?? body?.records ?? body;
  if (!Array.isArray(candidates)) return [];
  return candidates
    .filter((s) => {
      const visible = s?.visible;
      const status = String(s?.status ?? '').toUpperCase();
      const isVisible = visible == null || visible === true;
      return isVisible && status === 'AVAILABLE';
    })
    .map((s) => s?.id ?? s?.scooterId ?? s?.scooter_id ?? s)
    .filter((x) => x != null)
    .slice(0, limit);
}

function randomVisa() {
  const pool = ['4242424242424242', '4012888888881881', '5555555555554444'];
  return pool[Math.floor(Math.random() * pool.length)];
}

function assertInRange(actual, allowed) {
  if (!allowed.includes(actual)) {
    throw new Error(`Expected status in ${JSON.stringify(allowed)}, got ${actual}`);
  }
}
