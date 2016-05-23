/*
 * Copyright 2015-2016 by the authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.kussm.chain

import static de.kussm.chain.ChainLinkType.*
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Tests for {@link Chain}
 * @author Michael Kuß
 */
public class ChainSpec extends Specification {
  def 'Chain.of(ChainLink...) throws IllegalArgumentException if called without arguments'() {
    when:
    Chain.of()
    then:
    IllegalArgumentException ex = thrown(IllegalArgumentException)
    ex.message == 'chain must not be empty'
  }

  def 'Chain.of(ChainLink...) throws IllegalArgumentException if first chain link is a CONDITIONAL'() {
    when:
    Chain.of(CONDITIONAL, NORMAL)
    then:
    IllegalArgumentException ex = thrown(IllegalArgumentException)
    ex.message == 'chain must not start with a CONDITIONAL'
  }

  @Ignore // Legacy
  def 'Chain.of(ChainLink...) throws IllegalArgumentException if RECEIVER is not preceded by a TRANSMITTER'() {
    when:
    Chain.of(RECEIVER, NORMAL)
    then:
    IllegalArgumentException ex = thrown(IllegalArgumentException)
    ex.message == 'RECEIVER at index 0 is not preceded by a TRANSMITTER'
    when:
    Chain.of(NORMAL, RECEIVER, NORMAL)
    then:
    ex = thrown(IllegalArgumentException)
    ex.message == 'RECEIVER at index 1 is not preceded by a TRANSMITTER'
  }

  def 'Chain.of(ChainLink...) throws IllegalArgumentException if RECEIVER is followed by a TRANSMITTER'() {
    when:
    Chain.of(TRANSMITTER, RECEIVER, TRANSMITTER, RECEIVER)
    then:
    IllegalArgumentException ex = thrown(IllegalArgumentException)
    ex.message == 'RECEIVER at index 1 is followed by a TRANSMITTER'
  }

  @Ignore // Legacy
  def 'Chain.of(ChainLink...) throws IllegalArgumentException if TRANSMITTER is not followed by a RECEIVER'() {
    when:
    Chain.of(NORMAL, TRANSMITTER)
    then:
    IllegalArgumentException ex = thrown(IllegalArgumentException)
    ex.message == 'TRANSMITTER at index 1 is not followed by a RECEIVER'
    when:
    Chain.of(TRANSMITTER, NORMAL)
    then:
    ex = thrown(IllegalArgumentException)
    ex.message == 'TRANSMITTER at index 0 is not followed by a RECEIVER'
  }

  def 'Chain.of(ChainLink...) throws NullPointerException if chain link is null'() {
    when:
    Chain.of(NORMAL, null, NORMAL)
    then:
    NullPointerException ex = thrown(NullPointerException)
    ex.message == 'chain link at index 1 must not be null'
  }

  def 'Chain.of(ChainLink...) throws NullPointerException on null argument'() {
    when:
    Chain.of((ChainLinkType[]) null)
    then:
    NullPointerException ex = thrown(NullPointerException)
    ex.message == 'chainLinks must not be null'
  }

  def 'size()'() {
    expect:
    Chain.of(NORMAL).size()==1
    Chain.of((1..100).collect { NORMAL } as ChainLinkType[]).size()==100
  }

  def 'getFirstChainLink()'() {
    expect:
    Chain.of(NORMAL).getFirstChainLink()==NORMAL
  }

  def 'get(int) throws IndexOutOfBoundsException if index is invalid()'() {
    when:
    Chain.of(NORMAL, CONDITIONAL, TRANSMITTER, RECEIVER).get(-1)
    then:
    thrown(IndexOutOfBoundsException)
    when:
    Chain.of(NORMAL, CONDITIONAL, TRANSMITTER, RECEIVER).get(4)
    then:
    thrown(IndexOutOfBoundsException)
  }

  def 'get(int)'() {
    given:
    Chain c = Chain.of(NORMAL, CONDITIONAL, TRANSMITTER, RECEIVER)
    expect:
    c.get(0) == NORMAL
    c.get(1) == CONDITIONAL
    c.get(2) == TRANSMITTER
    c.get(3) == RECEIVER
  }

  def 'toString()'() {
    expect:
    Chain.of(NORMAL, CONDITIONAL, NORMAL).toString() == 'Chain(NCN)'
  }
}
