# StopWatch

Implemented in Kotlin. Timer runs in a background service. Clocks time in milliseconds.

Previous attempts:
1. Chronometer View: This view is built in and provides handy start and stop methods, but has precision only upto seconds.
2. TextView: Chronometer view inherits from TextView after all, so this can be used to implement milliseconds. Used a Handler to increment milliseconds and update
TextView. Still too slow due to the overhead of displaying to UI.
3. Background service: Runs separately from the main app. The service still can't update the UI every millisecond, it lags. Updates UI every 40 milliseconds, when
time variable is incremented by 40ms too. This is a lesser duration that human persistence of vision, so it still appears to change smoothly.
