# Accessibility Checklist (ID25)

This checklist targets practical WCAG 2.1 AA coverage for admin web and mobile (uni-app) surfaces.

## 1) Keyboard-Only Operation

- [ ] Every interactive control is reachable by keyboard in a logical order.
- [ ] A visible focus indicator appears for links, buttons, inputs, selects, pagination, and menu items.
- [ ] A skip-link is available and moves users to the main content area.
- [ ] Dialogs can be opened and completed without pointer interaction.
- [ ] No keyboard trap exists inside tables, date pickers, or dialogs.

## 2) Landmarks, Semantics, and Control Names

- [ ] Pages expose clear landmarks (`banner`, navigation, `main`, region headings).
- [ ] Form controls have explicit labels or accessible names.
- [ ] Data tables provide contextual descriptions for screen reader users.
- [ ] Action buttons include purpose-specific labels when context is row-dependent.
- [ ] Navigation active state is distinguishable by more than color alone.

## 3) Color Contrast and Visual Readability

- [ ] Body text and key labels meet contrast expectations against their backgrounds.
- [ ] Status colors (success/warning/error) remain readable with sufficient contrast.
- [ ] Critical messages are not conveyed by color only.
- [ ] Text size and spacing support readability on desktop and mobile.
- [ ] Tap targets on mobile pages are large enough for reliable interaction.

## 4) Error, Success, and Live Feedback

- [ ] Validation errors are shown close to affected controls with clear wording.
- [ ] Loading, success, and failure states are perceivable and announced where applicable.
- [ ] Important state changes (search results loaded, action completed, failures) are available to assistive technologies.
- [ ] Empty states are explicit and understandable without additional context.

## 5) Chart and Data Alternatives

- [ ] Each chart has a short human-readable description of purpose and axes/meaning.
- [ ] Key chart insights are also available as text summaries.
- [ ] Decorative graphics do not create redundant reading noise for screen readers.
- [ ] Statistical highlights remain understandable without visual chart interpretation.

## 6) Automated and Manual Verification

- [ ] Run automated a11y checks for admin feedback and revenue pages (`npm run test:a11y` in `auth_web_front`).
- [ ] Manually test tab flow from top of page through primary actions.
- [ ] Confirm focus style remains visible under high-density UI areas (tables/charts/dialogs).
- [ ] Verify color contrast in both normal and high-glare viewing conditions.
- [ ] Re-test after any UI refactor affecting layout, colors, or control composition.
