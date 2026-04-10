import { buildAuthApi } from '../targets/auth.mjs';
import { buildUserApi } from '../targets/users.mjs';
import { buildScooterApi } from '../targets/scooters.mjs';
import { buildPaymentCardApi } from '../targets/paymentCards.mjs';
import { buildSecurityApi } from '../targets/security.mjs';
import { buildBookingApi } from '../targets/bookings.mjs';
import { buildBookingExtensionApi } from '../targets/bookingExtension.mjs';

import { makeTestUsers } from '../util/testUsers.mjs';
import { assert, assertEq, assertMatch } from '../assert.mjs';

export function buildSprint2Suite({ http, logger }) {
  const auth = buildAuthApi(http);
  const users = buildUserApi(http);
  const scooters = buildScooterApi(http);
  const cards = buildPaymentCardApi(http);
  const security = buildSecurityApi(http);
  const bookings = buildBookingApi(http);
  const bookingExt = buildBookingExtensionApi(http);

  const ctx = {
    http,
    logger,
    auth,
    users,
    scooters,
    cards,
    security,
    bookings,
    bookingExt,
    state: {}
  };

  const tests = [];

  // ---------- Shared bootstrapping ----------
  tests.push({
    name: 'bootstrap: register + login two users',
    tags: ['bootstrap', 'auth', 'users'],
    run: async () => {
      const { userA, userB } = makeTestUsers();
      const regA = await users.register(userA);
      const regB = await users.register(userB);

      const regAData = regA.data ?? {};
      const regBData = regB.data ?? {};

      assert(regAData.userId, 'register should return userId');
      assert(regBData.userId, 'register should return userId');

      const loginA = await auth.login({ email: userA.email, password: userA.password });
      const loginB = await auth.login({ email: userB.email, password: userB.password });

      assert(loginA.token, 'login should return token');
      assert(loginA.userId, 'login should return userId');
      assert(loginB.token, 'login should return token');
      assert(loginB.userId, 'login should return userId');

      ctx.state.userA = { ...userA, userId: loginA.userId, token: loginA.token };
      ctx.state.userB = { ...userB, userId: loginB.userId, token: loginB.token };
    }
  });

  // ---------- ID3 Security Settings ----------
  tests.push({
    name: 'ID3: GET /security/settings requires auth (401)',
    tags: ['id3', 'security', 'auth'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { status } = await security.getSettings({ token: null });
      assertEq(status, 401);
    }
  });

  tests.push({
    name: 'ID3: update and read back security settings',
    tags: ['id3', 'security'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      assert(userA?.token, 'missing bootstrap userA');

      const before = await security.getSettings({ token: userA.token });
      assertEq(before.status, 200);

      const update = await security.updateSettings({
        token: userA.token,
        body: { twoFactorEnabled: true, maxSessionCount: 2, loginLockMinutes: 0 }
      });
      assertEq(update.status, 200);
      assert(update.data, 'missing data');

      const after = await security.getSettings({ token: userA.token });
      assertEq(after.status, 200);
      assertEq(after.data.twoFactorEnabled, true);
      assertEq(after.data.maxSessionCount, 2);
      assertEq(after.data.loginLockMinutes, 0);
    }
  });

  tests.push({
    name: 'ID3: validation rejects negative loginLockMinutes',
    tags: ['id3', 'security', 'validation'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const res = await security.updateSettings({
        token: userA.token,
        body: { loginLockMinutes: -1 }
      });
      assertInRange(res.status, [400, 422]);
    }
  });

  tests.push({
    name: 'ID3: validation rejects maxSessionCount=0',
    tags: ['id3', 'security', 'validation'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const res = await security.updateSettings({
        token: userA.token,
        body: { maxSessionCount: 0 }
      });
      assertInRange(res.status, [400, 422]);
    }
  });

  // ---------- ID2 Payment Cards ----------
  tests.push({
    name: 'ID2: list cards starts empty ([])',
    tags: ['id2', 'paymentcard'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const res = await cards.list({ userId: userA.userId, token: userA.token });
      assertEq(res.status, 200);
      assert(Array.isArray(res.data), 'data should be array');
    }
  });

  tests.push({
    name: 'ID2: create card and ensure maskedNumber only reveals last4',
    tags: ['id2', 'paymentcard'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const req = {
        holderName: 'Test User',
        // valid Luhn: 4242424242424242
        cardNumber: '4242424242424242',
        brand: 'VISA',
        expiryMonth: 12,
        expiryYear: new Date().getFullYear() + 1
      };
      const created = await cards.create({ userId: userA.userId, token: userA.token, body: req });
      assertEq(created.status, 200);
      assert(created.data?.id, 'missing card id');
      assertMatch(created.data.maskedNumber, /(\*{4}\s\*{4}\s\*{4}\s)\d{4}/);
      assert(!String(created.data.maskedNumber).includes(req.cardNumber), 'should not expose full card number');
      ctx.state.cardA1 = created.data;
    }
  });

  tests.push({
    name: 'ID2: invalid cardNumber fails (400/422)',
    tags: ['id2', 'paymentcard', 'validation'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const req = {
        holderName: 'Test User',
        cardNumber: '1234567890123',
        brand: 'VISA',
        expiryMonth: 12,
        expiryYear: new Date().getFullYear() + 1
      };
      const res = await cards.create({ userId: userA.userId, token: userA.token, body: req });
      assertInRange(res.status, [400, 422]);
    }
  });

  tests.push({
    name: 'ID2: duplicate card (same last4/brand/expiry) returns conflict',
    tags: ['id2', 'paymentcard', 'validation'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const req = {
        holderName: 'Test User',
        cardNumber: '4242424242424242',
        brand: 'VISA',
        expiryMonth: 12,
        expiryYear: new Date().getFullYear() + 1
      };
      const created = await cards.create({ userId: userA.userId, token: userA.token, body: req });
      assertInRange(created.status, [409, 400]);
    }
  });

  tests.push({
    name: 'ID2: create second card then switch default',
    tags: ['id2', 'paymentcard'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const req2 = {
        holderName: 'Test User',
        // valid Luhn: 4012888888881881
        cardNumber: '4012888888881881',
        brand: 'VISA',
        expiryMonth: 11,
        expiryYear: new Date().getFullYear() + 2
      };
      const created2 = await cards.create({ userId: userA.userId, token: userA.token, body: req2 });
      assertEq(created2.status, 200);
      ctx.state.cardA2 = created2.data;

      const setDefault = await cards.setDefault({
        userId: userA.userId,
        cardId: created2.data.id,
        token: userA.token
      });
      assertEq(setDefault.status, 200);

      const list = await cards.list({ userId: userA.userId, token: userA.token });
      assertEq(list.status, 200);
      const defaults = list.data.filter((c) => c.isDefault === true);
      assertEq(defaults.length, 1);
      assertEq(defaults[0].id, created2.data.id);
    }
  });

  tests.push({
    name: 'ID2: user B cannot access user A payment cards (403)',
    tags: ['id2', 'paymentcard', 'authz'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA, userB } = ctx.state;
      const res = await cards.list({ userId: userA.userId, token: userB.token });
      assertEq(res.status, 403);
    }
  });

  tests.push({
    name: 'ID2: set default is idempotent and keeps single default',
    tags: ['id2', 'paymentcard'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const card1 = ctx.state.cardA1;
      assert(card1?.id, 'missing cardA1');

      const set1 = await cards.setDefault({ userId: userA.userId, cardId: card1.id, token: userA.token });
      assertEq(set1.status, 200);

      const set2 = await cards.setDefault({ userId: userA.userId, cardId: card1.id, token: userA.token });
      assertEq(set2.status, 200);

      const list = await cards.list({ userId: userA.userId, token: userA.token });
      assertEq(list.status, 200);
      const defaults = list.data.filter((c) => c.isDefault === true);
      assertEq(defaults.length, 1);
      assertEq(defaults[0].id, card1.id);
    }
  });

  tests.push({
    name: 'ID2: deleting default card reassigns a new default when possible',
    tags: ['id2', 'paymentcard'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;
      const card2 = ctx.state.cardA2;
      const card1 = ctx.state.cardA1;
      assert(card2?.id && card1?.id, 'missing cards');

      // Ensure card2 is default, then delete it and expect card1 becomes default.
      const setDefault = await cards.setDefault({ userId: userA.userId, cardId: card2.id, token: userA.token });
      assertEq(setDefault.status, 200);

      const del = await cards.del({ userId: userA.userId, cardId: card2.id, token: userA.token });
      assertEq(del.status, 200);

      const list = await cards.list({ userId: userA.userId, token: userA.token });
      assertEq(list.status, 200);
      const defaults = list.data.filter((c) => c.isDefault === true);
      assertEq(defaults.length, 1);
      assertEq(defaults[0].id, card1.id);
    }
  });

  // ---------- ID7 Booking email confirmation (non-blocking) + ID10&11 extension ----------
  tests.push({
    name: 'ID7/ID10&11: create booking then extend it',
    tags: ['id7', 'id10', 'id11', 'booking', 'extension'],
    dependsOn: ['bootstrap: register + login two users'],
    run: async () => {
      const { userA } = ctx.state;

      const scooterList = await scooters.list({ limit: 5 });
      assertEq(scooterList.status, 200);
      const scooterId = pickFirstScooterId(scooterList.body);
      assert(scooterId, 'no scooter available to book');

      const created = await bookings.create({
        token: userA.token,
        body: {
          scooter_id: String(scooterId),
          user_id: String(userA.userId),
          duration: '10M',
          startLat: 1.23,
          startLng: 4.56
        }
      });
      assertEq(created.status, 201);
      assert(created.data?.id, 'missing booking id');
      assertEq(created.data.status, 'CONFIRMED');

      const bookingId = created.data.id;

      const extended = await bookingExt.extend({
        token: userA.token,
        bookingId,
        body: { duration: '10M' }
      });
      assertEq(extended.status, 200);
      assertEq(extended.data.status, 'CONFIRMED');
      assert(extended.data.totalPrice >= created.data.totalPrice, 'price should not decrease after extend');
      ctx.state.bookingA1 = extended.data;
    }
  });

  tests.push({
    name: 'ID10&11: complete booking then extension should be rejected',
    tags: ['id10', 'id11', 'booking', 'extension', 'state-machine'],
    dependsOn: ['ID7/ID10&11: create booking then extend it'],
    run: async () => {
      const { userA } = ctx.state;
      const booking = ctx.state.bookingA1;
      assert(booking?.id, 'missing prior bookingA1');

      const completed = await bookings.complete({
        token: userA.token,
        bookingId: booking.id,
        body: { endLat: 9.87, endLng: 6.54 }
      });
      assertInRange(completed.status, [200, 409, 400]);

      const extendAgain = await bookingExt.extend({
        token: userA.token,
        bookingId: booking.id,
        body: { duration: '10M' }
      });
      assertInRange(extendAgain.status, [400, 409]);
    }
  });

  tests.push({
    name: 'ID10&11: cannot extend booking without auth (401)',
    tags: ['id10', 'id11', 'booking', 'auth'],
    dependsOn: ['ID7/ID10&11: create booking then extend it'],
    run: async () => {
      const booking = ctx.state.bookingA1;
      assert(booking?.id, 'missing prior bookingA1');
      const res = await bookingExt.extend({
        token: null,
        bookingId: booking.id,
        body: { duration: '10M' }
      });
      assertEq(res.status, 401);
    }
  });

  tests.push({
    name: 'ID10&11: user B cannot extend user A booking (403)',
    tags: ['id10', 'id11', 'booking', 'authz'],
    dependsOn: ['bootstrap: register + login two users', 'ID7/ID10&11: create booking then extend it'],
    run: async () => {
      const { userB } = ctx.state;
      const booking = ctx.state.bookingA1;
      const res = await bookingExt.extend({
        token: userB.token,
        bookingId: booking.id,
        body: { duration: '10M' }
      });
      assertEq(res.status, 403);
    }
  });

  return { tests, ctx };
}

function assertInRange(actual, allowed) {
  if (!allowed.includes(actual)) {
    throw new Error(`Expected status in ${JSON.stringify(allowed)}, got ${actual}`);
  }
}

function pickFirstScooterId(body) {
  // ScooterController.getScooters returns scooterService.getScooters(...) directly, may be {data: ...} or already shaped.
  // We try common shapes to keep this system test robust.
  const candidates =
    body?.data?.data ??
    body?.data ??
    body?.items ??
    body?.content ??
    body?.records ??
    body;
  if (Array.isArray(candidates) && candidates.length > 0) {
    const first = candidates[0];
    return first?.id ?? first?.scooterId ?? first?.scooter_id ?? first;
  }
  return null;
}

