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

  // given:
  /scoreboard players set test_invert_with_true MplTest 0

  // when:
  /say this is true
  invert: /scoreboard players set test_invert_with_true MplTest 1

  // then:
  /scoreboard players test test_invert_with_true MplTest 0 0
  conditional: start test_invert_with_false
  invert: /say TEST FAILED
)

process test_invert_with_false (
  /say starting test_invert_with_false

  // given:
  /scoreboard players set test_invert_with_false MplTest 0

  // when:
  /this is false
  invert: /scoreboard players set test_invert_with_false MplTest 1

  // then:
  /scoreboard players test test_invert_with_true MplTest 1 1
  conditional: start test_waitfor_impulse
  invert: /say TEST FAILED
)

process test_waitfor_impulse (
  /say starting test_waitfor_impulse

  // given:
  /scoreboard players set MplTest MplTest 0
  start impulse_process

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 4 4
  conditional: start test_waitfor_repeat
  invert: /say TEST FAILED
)

process test_waitfor_repeat (
  /say starting test_waitfor_repeat

  // given:
  /scoreboard players set MplTest MplTest 0
  start repeat_process

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 3 3
  conditional: start test_conditional_waitfor_with_true
  invert: /say TEST FAILED
)

process test_conditional_waitfor_with_true (
  /say starting test_conditional_waitfor_with_true

  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /say this is true
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start test_conditional_waitfor_with_false
  invert: /say TEST FAILED
)

process test_conditional_waitfor_with_false (
  /say starting test_conditional_waitfor_with_false

  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /this is false
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start test_intercept
  invert: /say TEST FAILED
)

process test_intercept (
  /say starting test_intercept

  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start test_conditional_intercept_with_true
  invert: /say TEST FAILED
)

process test_conditional_intercept_with_true (
  /say starting test_conditional_intercept_with_true

  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start test_conditional_intercept_with_false
  invert: /say TEST FAILED
)

process test_conditional_intercept_with_false (
  /say starting test_conditional_intercept_with_false

  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start test_invert_intercept_with_true
  invert: /say TEST FAILED
)

process test_invert_intercept_with_true (
  /say starting test_invert_intercept_with_true

  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start test_invert_intercept_with_false
  invert: /say TEST FAILED
)

process test_invert_intercept_with_false (
  /say starting test_invert_intercept_with_false

  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start test_while_repeat_with_false
  invert: /say TEST FAILED
)

process test_while_repeat_with_false (
  /say starting test_while_repeat_with_false

  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /this is false
  repeat (
    /scoreboard players add MplTest MplTest 1
  )

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start test_while_repeat_with_5_iterations
  invert: /say TEST FAILED
)

process test_while_repeat_with_5_iterations (
  /say starting test_while_repeat_with_5_iterations

  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /scoreboard players test MplTest MplTest 1 5
  repeat (
    /scoreboard players add MplTest MplTest 1
  )

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start test_repeat_while_with_false
  invert: /say TEST FAILED
)

process test_repeat_while_with_false (
  /say starting test_repeat_while_with_false

  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat (
    /scoreboard players add MplTest MplTest 1
  ) do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start test_repeat_while_with_5_iterations
  invert: /say TEST FAILED
)

process test_repeat_while_with_5_iterations (
  /say starting test_repeat_while_with_5_iterations

  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat (
    /scoreboard players add MplTest MplTest 1
  ) do while: /scoreboard players test MplTest MplTest 1 5

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start finish
  invert: /say TEST FAILED
)

process finish (
  /say ALL TESTS SUCCESSFUL!!!
)

# Utility Prozesse

process setting_MplTest_to_5_and_calling_add_one_to_MplTest (
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
  /scoreboard players set MplTest MplTest 4
  notify
)

repeat process repeat_process (
  /say repeat process
  /scoreboard players set MplTest MplTest 3
  notify
  stop
)
