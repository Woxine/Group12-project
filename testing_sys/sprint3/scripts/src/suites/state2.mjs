import { buildAuthApi } from '../../../../sprint2/scripts/src/targets/auth.mjs';
import { buildUserApi } from '../../../../sprint2/scripts/src/targets/users.mjs';
import { makeTestUsers } from '../../../../sprint2/scripts/src/util/testUsers.mjs';
import { assert, assertEq } from '../../../../sprint2/scripts/src/assert.mjs';

import { buildFeedbackApi } from '../targets/feedback.mjs';
import { buildAdminApi } from '../targets/admin.mjs';

export function buildSprint3State2Suite({ http }) {
  const auth = buildAuthApi(http);
  const users = buildUserApi(http);
  const feedback = buildFeedbackApi(http);
  const admin = buildAdminApi(http);

  const ctx = { state: {} };
  const tests = [];

  tests.push({
    name: 'state2 bootstrap: login admin + register user + choose scooter',
    tags: ['state2', 'bootstrap', 'auth', 'admin'],
    run: async () => {
      const { userA } = makeTestUsers();
      const reg = await users.register(userA);
      assertInRange(reg.status, [200, 201]);

      const loginA = await auth.login({ email: userA.email, password: userA.password });
      const loginAdmin = await auth.login({ email: 'admin@admin.com', password: 'admin123' });
      assert(loginA?.token && loginA?.userId, 'state2 user login failed');
      assert(loginAdmin?.token && loginAdmin?.userId, 'state2 admin login failed');

      const scooterId = await ensureOneScooter({ adminApi: admin, adminToken: loginAdmin.token });
      assert(scooterId, 'need one scooter for feedback tests');

      ctx.state.user = loginA;
      ctx.state.admin = loginAdmin;
      ctx.state.scooterId = scooterId;
    }
  });

  tests.push({
    name: 'ID14: create LOW feedback then DIRECT_HANDLE success',
    tags: ['state2', 'id14', 'feedback', 'priority'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const { user, admin: adminUser, scooterId } = ctx.state;
      const created = await feedback.submit({
        token: user.token,
        body: {
          scooter_id: String(scooterId),
          description: 'LOW priority feedback from system test',
          location: 'zone-a',
          priority: 'LOW'
        }
      });
      assertEq(created.status, 201);
      assert(created.data?.id, 'feedback id missing');
      ctx.state.lowFeedbackId = created.data.id;

      const processed = await feedback.processPriority({
        token: adminUser.token,
        feedbackId: created.data.id,
        body: { action: 'DIRECT_HANDLE', note: 'handled by admin' }
      });
      assertEq(processed.status, 200);
      assertEq(processed.data?.status, 'DIRECT_HANDLED');
      assertEq(Boolean(processed.data?.escalated), false);
    }
  });

  tests.push({
    name: 'ID14: create HIGH feedback and ESCALATE success',
    tags: ['state2', 'id14', 'feedback', 'escalate'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const { user, admin: adminUser, scooterId } = ctx.state;
      const created = await feedback.submit({
        token: user.token,
        body: {
          scooter_id: String(scooterId),
          description: 'HIGH priority feedback from system test',
          location: 'zone-b',
          priority: 'HIGH'
        }
      });
      assertEq(created.status, 201);
      assert(created.data?.id, 'feedback id missing');
      ctx.state.highFeedbackId = created.data.id;

      const escalated = await feedback.processPriority({
        token: adminUser.token,
        feedbackId: created.data.id,
        body: { action: 'ESCALATE', escalateTo: 'ops-l2', note: 'escalated by rule' }
      });
      assertEq(escalated.status, 200);
      assertEq(escalated.data?.status, 'ESCALATED');
      assertEq(Boolean(escalated.data?.escalated), true);
    }
  });

  tests.push({
    name: 'ID14: high priority DIRECT_HANDLE is rejected',
    tags: ['state2', 'id14', 'validation'],
    dependsOn: ['ID14: create HIGH feedback and ESCALATE success'],
    run: async () => {
      const { admin: adminUser, highFeedbackId } = ctx.state;
      const res = await feedback.processPriority({
        token: adminUser.token,
        feedbackId: highFeedbackId,
        body: { action: 'DIRECT_HANDLE' }
      });
      assertInRange(res.status, [400, 409]);
    }
  });

  tests.push({
    name: 'ID14: non-admin cannot process feedback by priority',
    tags: ['state2', 'id14', 'authz'],
    dependsOn: ['ID14: create LOW feedback then DIRECT_HANDLE success'],
    run: async () => {
      const { user, lowFeedbackId } = ctx.state;
      const res = await feedback.processPriority({
        token: user.token,
        feedbackId: lowFeedbackId,
        body: { action: 'DIRECT_HANDLE' }
      });
      assertEq(res.status, 403);
    }
  });

  tests.push({
    name: 'ID15: list high priority escalated issues',
    tags: ['state2', 'id15', 'feedback', 'query'],
    dependsOn: ['ID14: create HIGH feedback and ESCALATE success'],
    run: async () => {
      const { admin: adminUser, highFeedbackId } = ctx.state;
      const res = await feedback.listHighPriority({
        token: adminUser.token,
        escalated: true,
        page: 1,
        size: 20
      });
      assertEq(res.status, 200);

      const rows = Array.isArray(res.data) ? res.data : res.body?.data ?? [];
      assert(Array.isArray(rows), 'high priority list should be array');
      assert(rows.some((r) => String(r.feedbackId) === String(highFeedbackId)), 'should include escalated high priority issue');
    }
  });

  tests.push({
    name: 'ID15: high priority API requires admin',
    tags: ['state2', 'id15', 'authz'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const { user } = ctx.state;
      const res = await feedback.listHighPriority({ token: user.token, escalated: true });
      assertEq(res.status, 403);
    }
  });

  tests.push({
    name: 'ID20: admin gets popular rental dates leaderboard',
    tags: ['state2', 'id20', 'admin', 'revenue'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const { admin: adminUser } = ctx.state;
      const res = await admin.popularDatesWeek({ token: adminUser.token });
      assertEq(res.status, 200);
      assert(Array.isArray(res.data), 'popular dates should return an array');
      if (res.data.length > 0) {
        assert(res.data[0].date != null, 'popular date item.date missing');
        assert(res.data[0].rank != null, 'popular date item.rank missing');
        assert(res.data[0].revenue != null, 'popular date item.revenue missing');
      }
    }
  });

  tests.push({
    name: 'ID20: popular rental dates API requires admin',
    tags: ['state2', 'id20', 'authz'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const { user } = ctx.state;
      const res = await admin.popularDatesWeek({ token: user.token });
      assertEq(res.status, 403);
    }
  });

  tests.push({
    name: 'ID25: accessibility baseline via API auth checks remains stable',
    tags: ['state2', 'id25', 'baseline'],
    dependsOn: ['state2 bootstrap: login admin + register user + choose scooter'],
    run: async () => {
      const noTokenHighPriority = await feedback.listHighPriority({ token: null, escalated: true });
      const noTokenPopularDates = await admin.popularDatesWeek({ token: null });
      assertEq(noTokenHighPriority.status, 401);
      assertEq(noTokenPopularDates.status, 401);
    }
  });

  return { tests, ctx };
}

async function ensureOneScooter({ adminApi, adminToken }) {
  const list = await adminApi.listScooters({ token: adminToken, limit: 20 });
  assertEq(list.status, 200);
  let scooterId = pickFirstScooterId({ data: list.data });
  if (scooterId) return scooterId;

  const create = await adminApi.createScooter({
    token: adminToken,
    body: {
      status: 'AVAILABLE',
      type: 'GEN1',
      hour_rate: 5.0,
      location_lat: 1.31,
      location_lng: 103.81,
      location_name: `SYS-TEST-${Date.now()}`
    }
  });
  assertInRange(create.status, [200, 201]);
  return create.data?.id ?? create.body?.data?.id ?? null;
}

function pickFirstScooterId(body) {
  const candidates = body?.data?.data ?? body?.data ?? body?.items ?? body?.content ?? body?.records ?? body;
  if (Array.isArray(candidates) && candidates.length > 0) {
    const first = candidates[0];
    return first?.id ?? first?.scooterId ?? first?.scooter_id ?? first;
  }
  return null;
}

function assertInRange(actual, allowed) {
  if (!allowed.includes(actual)) {
    throw new Error(`Expected status in ${JSON.stringify(allowed)}, got ${actual}`);
  }
}
