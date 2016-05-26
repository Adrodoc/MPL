install (
  /setblock ${origin + (-2 0 0)} command_block 0 replace {Command:execute @e[name=mpl_tests] ~ ~ ~ setblock ~ ~ ~ redstone_block}
  /scoreboard objectives add MplTest dummy
)

uninstall (
  /scoreboard objectives remove MplTest
)

process mpl_tests (
  /say starting mpl test
  start test_invert_with_true
)

process test_invert_with_true (
  /say starting test_invert_with_true
  /scoreboard players set test_invert_with_true MplTest 0
  /say this is always true
  invert: /scoreboard players set test_invert_with_true MplTest 1
  /scoreboard players test test_invert_with_true MplTest 0 0
  conditional: /say test_invert successful!
  conditional: start test_invert_with_false
)

process test_invert_with_false (
  /say starting test_invert_with_false
  /false
  invert: /say test_invert_with_false failed!
  start test_waitfor_impulse
)

process test_waitfor_impulse (
  /say starting test_waitfor_impulse
  start impulse_process
  waitfor
  /say test_waitfor_impulse successful!
  start test_waitfor_repeat
)

process test_waitfor_repeat (
  /say starting test_waitfor_repeat
  start repeat_process
  waitfor
  /say test_waitfor_repeat successful!
  start test_conditional_waitfor_with_true
)

process test_conditional_waitfor_with_true (
  /say starting test_conditional_waitfor_with_true
  /scoreboard players reset MplTest MplTest
  start add_one_to_MplTest
  /say true
  conditional: waitfor
  /scoreboard players test MplTest MplTest 1 1
  conditional: /say test_conditional_waitfor_with_true successful!
  conditional: start test_conditional_waitfor_with_false
)

process test_conditional_waitfor_with_false (
  /say starting test_conditional_waitfor_with_false
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest
  /false
  conditional: waitfor
  /scoreboard players test MplTest MplTest 0 0
  conditional: /say test_conditional_waitfor_with_false successful!
  conditional: start test_intercept
)

process test_intercept (
  /say starting test_intercept
  /scoreboard players set MplTest MplTest 0
  start calling_add_one_to_MplTest_and_setting_MplTest_to_5
  intercept add_one_to_MplTest
  /scoreboard players test MplTest MplTest 5 5
  conditional: /say test_intercept successful!
  conditional: start test_conditional_intercept_with_true
)

process test_conditional_intercept_with_true (
  /say starting test_conditional_intercept_with_true
  /scoreboard players set MplTest MplTest 0
  start calling_add_one_to_MplTest_and_setting_MplTest_to_5
  /say true
  conditional: intercept add_one_to_MplTest
  /scoreboard players test MplTest MplTest 5 5
  conditional: /say test_conditional_intercept_with_true successful!
  conditional: start test_conditional_intercept_with_false
)

process test_conditional_intercept_with_false (
  /say starting test_conditional_intercept_with_false
  /scoreboard players set MplTest MplTest 0
  start calling_add_one_to_MplTest_and_setting_MplTest_to_5
  /false
  conditional: intercept add_one_to_MplTest
  /scoreboard players test MplTest MplTest 0 0
  conditional: /say test_conditional_intercept_with_false successful!
  conditional: start test_invert_intercept_with_true
)

process test_invert_intercept_with_true (
  /say starting test_invert_intercept_with_true
  /scoreboard players set MplTest MplTest 0
  start calling_add_one_to_MplTest_and_setting_MplTest_to_5
  /say true
  invert: intercept add_one_to_MplTest
  /scoreboard players test MplTest MplTest 0 0
  conditional: /say test_invert_intercept_with_true successful!
  conditional: start test_invert_intercept_with_false
)

process test_invert_intercept_with_false (
  /say starting test_invert_intercept_with_false
  /scoreboard players set MplTest MplTest 0
  start calling_add_one_to_MplTest_and_setting_MplTest_to_5
  /false
  invert: intercept add_one_to_MplTest
  /scoreboard players test MplTest MplTest 5 5
  conditional: /say test_invert_intercept_with_false successful!
  conditional: start test_while_repeat_with_false
)

process test_while_repeat_with_false (
  /say starting test_while_with_true
  /scoreboard players set MplTest MplTest 1

  while: /false
  repeat (
    /scoreboard players add MplTest MplTest 1
  )

  /scoreboard players test MplTest MplTest 1 1
  conditional: start test_while_repeat_with_5_iterations
)

process test_while_repeat_with_5_iterations (
  /say starting test_while_with_true
  /scoreboard players set MplTest MplTest 1

  while: /scoreboard players test MplTest MplTest 1 5
  repeat (
    /scoreboard players add MplTest MplTest 1
  )

  /scoreboard players test MplTest MplTest 6 6
  conditional: start test_while_repeat_with_false
)

process test_while_repeat_with_false (
  /say starting test_while_with_true
  /scoreboard players set MplTest MplTest 1

  repeat (
    /scoreboard players add MplTest MplTest 1
  ) while: /false

  /scoreboard players test MplTest MplTest 2 2
  conditional: start test_repeat_while_with_5_iteration
)

process test_repeat_while_with_5_iterations (
  /say starting test_while_with_true
  /scoreboard players set MplTest MplTest 1

  repeat (
    /scoreboard players add MplTest MplTest 1
  ) while: /scoreboard players test MplTest MplTest 1 5

  /scoreboard players test MplTest MplTest 6 6
  conditional: start finish
)

process finish (
  /say ALL TESTS SUCCESSFUL!!!
)

# Utility Prozesse

process calling_add_one_to_MplTest_and_setting_MplTest_to_5 (
  # 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  # 1 tick delay end
  /scoreboard players set MplTest MplTest 5
  start add_one_to_MplTest
)

process add_one_to_MplTest (
  # 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  # 1 tick delay end
  /scoreboard players add MplTest MplTest 1
  notify
)

impulse process impulse_process (
  /say impulse process
  notify
)

repeat process repeat_process (
  /say repeat process
  notify
  stop
)
