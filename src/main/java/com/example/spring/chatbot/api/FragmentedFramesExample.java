package com.example.spring.chatbot.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.Opcode;

public class FragmentedFramesExample {

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    // WebSocketImpl.DEBUG = true; // will give extra output

    WebSocketClient websocket = new ExampleClient(new URI("ws://localhost:8887"));
    if (!websocket.connectBlocking()) {
      System.err.println("Could not connect to the server.");
      return;
    }

    System.out.println("This example shows how to send fragmented(continuous) messages.");

    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    while (websocket.isOpen()) {
      System.out
          .println("Please type in a loooooong line(which then will be send in 2 byte fragments):");
      String longline = stdin.readLine();
      ByteBuffer longelinebuffer = ByteBuffer.wrap(longline.getBytes());
      longelinebuffer.rewind();

      for (int position = 2; ; position += 2) {
        if (position < longelinebuffer.capacity()) {
          longelinebuffer.limit(position);
          websocket.sendFragmentedFrame(Opcode.TEXT, longelinebuffer,
              false);// when sending binary data one should use Opcode.BINARY
          assert (longelinebuffer.remaining() == 0);
          // after calling sendFragmentedFrame one may reuse the buffer given to the method immediately
        } else {
          longelinebuffer.limit(longelinebuffer.capacity());
          websocket
              .sendFragmentedFrame(Opcode.TEXT, longelinebuffer, true);// sending the last frame
          break;
        }

      }
      System.out.println("You can not type in the next long message or press Ctr-C to exit.");
    }
    System.out.println("FragmentedFramesExample terminated");
  }
}