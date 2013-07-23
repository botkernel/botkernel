botkernel
=========

A Java based Reddit Bot Kernel

## Dependencies

  [The botkernel jReddit fork](https://github.com/botkernel/jReddit)

  Clone this dependency at the same dir level you clone botkernel
  and the default paths should work fine without reconfiguring. 

  E.g.

    joe@foo:~/dev$ ls
    botkernel  jReddit  
    joe@foo:~/dev$ 

  Build the dependency

## Building

    ant

## Running the example

  This includes an example bot.
  To run the example, configure bot credentials and other properties
  specific to your usernames and subreddits in the
  ./scratch/*.properties files for your admin bot (a bot that acts as
  administrator of your bot kernel server) and for your sample bot.

  Then login as your "owner" user and send a Private Message to the admin bot 
  with the text:
    
    loadbot com.jreddit.botkernel.SampleBot

  This should load the SampleBot into your botkernel server.


