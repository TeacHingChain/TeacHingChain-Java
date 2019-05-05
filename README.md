TeacHingChain-Java - A very simple implementation of a rudimentary blockchain for illustrative/teaching purposes in Java 

Current build is stable but **LOCAL ONLY**, the websocket integration for block/tx transport is not yet working

This means you can only run it as a 'private blockchain' at this time

**INSTRUCTIONS FOR BUILDING/RUNNING PROJECT**

1.  Use the genesis builder in the GenesisBuilder-Java repo to generate the genesis block and take note of the values.. OR you can use the one provided

2.  If you're building your own genesis block, navigate to the GenesisBlock.java class in com.thc.blockchain.wallet

3.  Replace the appropriate values (algo (only sha256 works right now), timestamp, pszTimestamp, genesis hash, difficulty)

4.  On first run, the blockchain will be initialized and all of the appropriate keys will be generated and the program will exit

5.  Rerun the program and you will be greeted with the command prompt

**NOTES**

1.  We have added support for the maven dependencies plugin, so the classpath is now in the build directory 

2.  This project is in it's infancy, there are many things that are being added/changed/worked on.. This is never meant to be production quality software, it's meant to be very simple and for illustrative purposes.. 

3.  We are amateur coders, there will be mistakes and better ways to code some of the elements of this project. Feel free to give us your thoughts on any of the code or any part of the project!

**IN THE WORKS**

1.  Adding web socket client and server end-points for block/tx transport across nodes using server push

2.  Began building GUI, only send tx and mining functionality, not yet separate from CLI (they still run concurrently)

3.  Adding support for SHA512 and scrypt algorithms

4.  Rewriting the difficulty adjustment (which is very granular and not at all reminiscent of an actual difficulty adjustment algorithm)

5.  Updating the miner class to allow for the discovery of tx's in the tx-pool, as well as the ability to throw away stale work

6.  Adding support for SHA512 and scrypt algorithms for the GenesisBuilder (in the GenesisBuilder-Java repo)

7.  Adding the ability to run the wallet as the GUI or as a daemon with a CLI

8.  Writing shell scripts for easy building/running (since the project will need to run as both a deployed war AND a jar when the network side is working)

8.  And more!

**JOIN THE COMMUNITY, CONTRIBUTE!**

If you are interested in contributing, email us @ TeacHingChain0420@gmail.com

We are open to suggestions, comments and contributions so please feel free to contact us!

More detailed technical specifications on the project are coming soon!
