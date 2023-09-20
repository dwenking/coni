package coni;

import coni.connector.conn.Conn;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;

public class Main {
    public static void main(String[] args) {

        Class<?>[] classes = {DriverManager.class};

        // 遍历每个类
        for (Class<?> cls : classes) {
            System.out.println("Class: " + cls.getName());

            // 获取类的所有方法
            Method[] methods = cls.getMethods();

            // 遍历每个方法
            for (Method method : methods) {
                System.out.print("  Method: " + method.getName());

                // 获取方法的参数
                Parameter[] parameters = method.getParameters();

                // 遍历每个参数
                for (Parameter parameter : parameters) {
                    System.out.print(" [" + parameter.getType().getSimpleName() + " " + parameter.getName() + "]");
                }

                System.out.println();
            }
        }


    }
}
