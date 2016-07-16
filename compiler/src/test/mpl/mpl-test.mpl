install {
  /setblock ${origin + (-2 0 0)} command_block 0 replace {Command:execute @e[name=mpl_tests] ~ ~ ~ setblock ~ ~ ~ redstone_block}
  /scoreboard objectives add MplTest dummy
  /scoreboard objectives setdisplay sidebar MplTest
}

uninstall {
  /scoreboard objectives remove MplTest
  /kill @e[name=continue_tests]
}

process continue_tests {}

process mpl_tests {
  /say starting mpl test
  /scoreboard players tag @e[tag=Test] add param
  while: /testfor @e[tag=param]
  repeat {
    /scoreboard players tag @e[tag=param,c=1] add current
    /scoreboard players tag @e[tag=current] remove param
    /tellraw @a [{"text":"Starting "},{"selector":"@e[tag=current]","color":"yellow"}]
    /execute @e[tag=current] ~ ~ ~ setblock ~ ~ ~ redstone_block
    waitfor continue_tests
    /kill @e[name=continue_tests]
    /scoreboard players tag @e[tag=current] remove current
  }
  /say ALL TESTS SUCCESSFUL!!!
}

#Test
process test_invert_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /say this is true
  invert: /scoreboard players set MplTest MplTest 1

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_invert_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  /this is false
  invert: /scoreboard players set MplTest MplTest 1

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_waitfor_impulse {
  // given:
  /scoreboard players set MplTest MplTest 0
  start impulse_process

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 4 4
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_waitfor_repeat {
  // given:
  /scoreboard players set MplTest MplTest 0
  start repeat_process

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 3 3
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_conditional_waitfor_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /say this is true
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_conditional_waitfor_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /this is false
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_intercept {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_conditional_intercept_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_conditional_intercept_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  conditional: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_invert_intercept_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /say this is true
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_invert_intercept_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0

  // when:
  start setting_MplTest_to_5_and_calling_add_one_to_MplTest
  /this is false
  invert: intercept add_one_to_MplTest

  // then:
  /scoreboard players test MplTest MplTest 5 5
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_while_repeat_with_false {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /this is false
  repeat {
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_while_repeat_with_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  while: /scoreboard players test MplTest MplTest 1 5
  repeat {
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_while_with_false {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_while_with_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
  } do while: /scoreboard players test MplTest MplTest 1 5

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_with_unconditional_break {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_with_conditional_break_after_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    conditional: break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_with_invert_break_after_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    invert: break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_nested_break_with_label {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  outer: repeat {
    repeat {
      break outer
    }
    /scoreboard players add MplTest MplTest 1
  }

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_conditional_continue_checks_the_condition {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    conditional: continue
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_invert_continue_checks_the_condition {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    invert: continue
  } do while: /this is false

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_with_conditional_continue_for_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 1 5
    conditional: continue
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_repeat_with_invert_continue_for_5_iterations {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  repeat {
    /scoreboard players add MplTest MplTest 1
    /scoreboard players test MplTest MplTest 6 *
    invert: continue
    break
  }

  // then:
  /scoreboard players test MplTest MplTest 6 6
  conditional: start continue_tests
  invert: /say TEST FAILED
}

#Test
process test_nested_continue_with_label {
  // given:
  /scoreboard players set MplTest MplTest 1

  // when:
  outer: while: /scoreboard players test MplTest MplTest * 1
  repeat {
    /scoreboard players add MplTest MplTest 1
    repeat {
      continue outer
    }
    /scoreboard players add MplTest MplTest 10
  }

  // then:
  /scoreboard players test MplTest MplTest 2 2
  conditional: start continue_tests
  invert: /say TEST FAILED
}

// Utility Prozesse

process setting_MplTest_to_5_and_calling_add_one_to_MplTest {
  // 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  // 1 tick delay end
  /scoreboard players set MplTest MplTest 5
  start add_one_to_MplTest
}

process add_one_to_MplTest {
  // 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  // 1 tick delay end
  /scoreboard players add MplTest MplTest 1
  notify
}

impulse process impulse_process {
  /say impulse process
  /scoreboard players set MplTest MplTest 4
  notify
}

repeat process repeat_process {
  /say repeat process
  /scoreboard players set MplTest MplTest 3
  notify
  stop
}
