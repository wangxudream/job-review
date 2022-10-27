package org.kataer.review.program.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

public class User {
  public static void main(String[] args) {
    User user = new User();
    System.out.println(ClassLayout.parseInstance(user).toPrintable());
    System.out.println(GraphLayout.parseInstance(user).totalSize());
    User[] users = new User[2];
    users[0] = user;
    System.out.println(ClassLayout.parseInstance(users).toPrintable());
  }
}
