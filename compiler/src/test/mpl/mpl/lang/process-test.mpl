include "../../mpl/unit/mpl-unit.mpl"
import "../../mpl/test/test-utils.mpl"

#Test
impulse process test_calling_a_remote_process_takes_two_ticks {
  // given:
  /scoreboard players set MplTest MplTest 0
  /scoreboard players set Tick MplTest 0
  start tick_counter

  // when:
  add_one_to_MplTest_remote()

  // then:
  stop tick_counter
  /scoreboard players test MplTest MplTest 1 1
  conditional: /scoreboard players test Tick MplTest 2 2
  conditional: notify continue_tests
  invert: start fail
}

impulse process add_one_to_MplTest_remote {
  /scoreboard players add MplTest MplTest 1
}



#Test
impulse process test_calling_an_inline_process_is_instantanious {
  // given:
  /scoreboard players set MplTest MplTest 0
  /scoreboard players set Tick MplTest 0
  start tick_counter

  // when:
  add_one_to_MplTest_inline()

  // then:
  stop tick_counter
  /scoreboard players test MplTest MplTest 1 1
  conditional: /scoreboard players test Tick MplTest 0 0
  conditional: notify continue_tests
  invert: start fail
}

inline process add_one_to_MplTest_inline {
  /scoreboard players add MplTest MplTest 1
}
