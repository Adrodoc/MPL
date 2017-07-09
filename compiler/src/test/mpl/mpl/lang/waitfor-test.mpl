include "../../mpl/unit/mpl-unit.mpl"
import "../../mpl/test/test-utils.mpl"

#Test
impulse process test_waitfor_impulse {
  // given:
  /scoreboard players set MplTest MplTest 0
  start impulse_process_to_wait_for

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 4 4
  conditional: notify continue_tests
  invert: start fail
}

impulse process impulse_process_to_wait_for {
  /say impulse process
  /scoreboard players set MplTest MplTest 4
}



#Test
impulse process test_waitfor_repeat {
  // given:
  /scoreboard players set MplTest MplTest 0
  start repeat_process_to_wait_for

  // when:
  waitfor

  // then:
  /scoreboard players test MplTest MplTest 3 3
  conditional: notify continue_tests
  invert: start fail
}

repeat process repeat_process_to_wait_for {
  /say repeat process
  /scoreboard players set MplTest MplTest 3
  notify repeat_process_to_wait_for
  stop
}



#Test
impulse process test_conditional_waitfor_with_true {
  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /say this is true
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 1 1
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_conditional_waitfor_with_false {
  // given:
  /scoreboard players set MplTest MplTest 0
  start add_one_to_MplTest

  // when:
  /this is false
  conditional: waitfor

  // then:
  /scoreboard players test MplTest MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

#Test
impulse process test_waitfor_in_repeat_process {
  // given:
  /scoreboard players set MplTest MplTest 0
  start repeatProcessMitWaitfor
  wait2ticks()

  // when:
  notify event

  // then:
  wait2ticks()
  stop repeatProcessMitWaitfor
  /kill @e[name=event_NOTIFY]
  /scoreboard players test MplTest MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

repeat process repeatProcessMitWaitfor {
  /scoreboard players add MplTest MplTest 1
  waitfor event
}
