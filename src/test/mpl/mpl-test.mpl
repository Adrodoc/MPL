install
/scoreboard objectives add MplTest dummy

uninstall
/scoreboard objectives remove MplTest

process mpl_tests:
/say starting mpl test
start test_invert_with_true

process test_invert_with_true:
/scoreboard players set test_invert_with_true MplTest 0
/testfor @p
invert: /scoreboard players set test_invert_with_true MplTest 1
/scoreboard players test test_invert_with_true MplTest 0 0
conditional: /say test_invert successful!
conditional: start test_invert_with_false

process test_invert_with_false:
/testforblock ~ ~ ~ stone
invert: /say test_invert_with_false failed!
start test_waitfor_impulse

process test_waitfor_impulse:
/say starting test_waitfor_impulse
start impulse_process
waitfor
/say test_waitfor_impulse successful!
start test_waitfor_repeat

process test_waitfor_repeat:
start repeat_process
waitfor
/say test_waitfor_repeat successful!
start finish

process finish:
/say ALL TESTS SUCCESSFUL!!!

# Utility Prozesse

impulse process impulse_process:
/say impulse process
notify

repeat process repeat_process:
/say repeat process
notify
stop
