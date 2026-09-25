package com.example

import com.example.blockchain.BlockchainEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGenesisBlockCreationAndValidation() {
    val genesis = BlockchainEngine.createGenesisBlock()
    assertEquals(0L, genesis.index)
    assertEquals(BlockchainEngine.GENESIS_PREV_HASH, genesis.previousHash)
    assertTrue(genesis.hash.startsWith("00"))

    val audit = BlockchainEngine.validateChain(listOf(genesis))
    assertTrue(audit.isValid)
    assertEquals(1, audit.totalBlocks)
  }
}
