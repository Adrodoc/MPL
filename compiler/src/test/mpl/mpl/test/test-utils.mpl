
// Utility Prozesse

repeat process tick_counter {
  /scoreboard players add Tick MplTest 1
}

impulse process wait2ticks {}

impulse process setting_MplTest_to_5_and_calling_add_one_to_MplTest {
  // 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  // 1 tick delay end
  /scoreboard players set MplTest MplTest 5
  start add_one_to_MplTest
}

impulse process add_one_to_MplTest {
  // 1 tick delay
  /setblock ${this+1} redstone_block
  skip
  impulse: /setblock ${this-1} stone
  // 1 tick delay end
  /scoreboard players add MplTest MplTest 1
}

impulse process add_one_to_MplTest_impulse {
  /scoreboard players add MplTest MplTest 1
}

inline process add_one_to_MplTest_inline {
  /scoreboard players add MplTest MplTest 1
}
