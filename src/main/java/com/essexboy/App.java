package com.essexboy;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            if (args.length == 2 && args[1].equalsIgnoreCase("getConsentUrl")) {
                BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(args[0]);
                blackBoxTestRunner.getConsentUrl();
            } else if (args.length == 3 && args[1].equalsIgnoreCase("getRefreshToken")) {
                BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(args[0]);
                try {
                    blackBoxTestRunner.getRefreshToken(args[2]);
                } catch (Exception e) {
                    System.out.println("error invalid consent Id");
                }
            } else if (args.length == 2 && args[1].equalsIgnoreCase("getToken")) {
                BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(args[0]);
                try {
                    blackBoxTestRunner.getToken();
                } catch (Exception e) {
                    System.out.println("error invalid refresh token");
                }
            } else if (args.length == 1) {
                BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(args[0]);
                blackBoxTestRunner.runTests();
                blackBoxTestRunner.runReport();
                blackBoxTestRunner.runLoadReport();
            } else {
                help();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            help();
        }
    }

    private static void help() {
        System.out.println("");
        System.out.println("usage : java -jar black-box-tester-1.0.jar <test file> [function] [value]");
        System.out.println("");
        System.out.println("where function:");
        System.out.println("\tgetConsentUrl");
        System.out.println("\tgetRefreshToken");
        System.out.println("\t\tvalue=authentication code");
        System.out.println("\tgetToken");
        System.out.println("");
        System.exit(0);
    }
}
