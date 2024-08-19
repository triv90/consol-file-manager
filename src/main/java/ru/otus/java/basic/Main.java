package ru.otus.java.basic;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Scanner;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;


public class Main {
    static Integer findResult = 0;
    public static void main(String[] args) {
        Path path = getCurrentDir();
        String strEmpty = "";
        String[] commands;
        print(path);
        while (true) {
            strEmpty = inputString();
            commands = strEmpty.split(" ");
            if (commands[0].equals("ls")) {
                if (commands.length == 2) {
                    commandLs(path, commands[1]);
                } else if (commands.length == 1) {
                    commandLs(path);
                }
                print(path);
            } else if (commands[0].equals("cd")) {
                path = commandCd(path, commands[1]);
                print(path);
            } else if (commands[0].equals("mkdir")) {
                commandMkDir(path, commands[1]);
                print(path);
            } else if (commands[0].equals("rm")) {
                commandRm(commands[1]);
                print(path);
            } else if (commands[0].equals("cp")) {
                if (commands.length == 3) {
                    commandCp(commands[1], commands[2]);
                } else if (commands.length == 4) {
                    commandCp(commands[1], commands[2], commands[2]);
                }
                print(path);
            } else if (commands[0].equals("mv")) {
                if (commands.length == 3) {
                    commandMv(commands[1], commands[2]);
                } else if (commands.length == 4) {
                    commandMv(commands[1], commands[2], commands[3]);
                }
                print(path);
            } else if (commands[0].equals("finfo")) {
                commandFinfo(path, commands[1]);
                print(path);
            } else if (commands[0].equals("find")) {
                String beginPath = String.valueOf(path);
                File searchFile;
                Integer root = 0;
                findResult = 0;
                searchFile = new File(beginPath, commands[1]);
                if (searchFile.isFile()) {
                    System.out.println("   " + searchFile.getPath());
                    root += 1;
                }
                findResult = root + findFile(beginPath, commands[1]);
                if (findResult == 0) {
                    System.out.println("Файл не найден");
                } else {
                    System.out.println("Найдено файлов: " + findResult);
                }
                print(path);
            } else if (commands[0].equals("help")) {
                Path helpPath = Paths.get(getCurrentDir().toString(), "src", "main", "resources", "help.txt");
                printFile(String.valueOf(helpPath));
                print(path);
            } else if (Arrays.asList("exit").contains(commands[0])) {
                break;
            } else if (strEmpty.equals("")) {
                System.out.print(path + ":");
            } else {
                System.out.println("Неизвестная команда");
                print(path);
            }
        }
    }

    private static Integer findFile(String path, String fileName) {
        File dir = new File(path);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                String childFolderName = String.valueOf(Paths.get(file.getParent(), file.getName()));
                File searchFile = new File(childFolderName, fileName);
                if (searchFile.isFile()) {
                    System.out.println("   " + searchFile.getPath());
                    findResult += 1;
                }
                findFile(childFolderName, fileName);
            }
        }
        return findResult;
    }

    public static void commandFinfo(Path path, String fileName) {
        try {
            File file = new File(fileName);
            Path fullPath = Paths.get(String.valueOf(path), fileName);
            BasicFileAttributes attr;
            attr = Files.readAttributes(fullPath, BasicFileAttributes.class);
            System.out.println("   Путь: " + file.getParent());
            System.out.println("   Размер: " + file.length() + " байт");
            System.out.println("   Создан: " + attr.creationTime());
            System.out.println("   Изменен: " + attr.lastModifiedTime());
        } catch (
                FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка вывода");
        }
    }

    public static Path commandCd(Path path, String command) {
        if (command.equals("..")) {
            try {
                if (Files.exists(path.getParent())) {
                    return path.getParent();
                }
            } catch (NullPointerException e) {
                return path;
            }
        } else {
            try {
                command = String.valueOf(Paths.get(String.valueOf(path), command));
                if (Files.exists(Path.of(command))) {
                    return Path.of(command);
                }
            } catch (InvalidPathException e) {
                System.out.println("Неверный путь");
            }
        }
        return path;
    }

    public static void commandMkDir(Path path, String newFolder) {
        String currentPath = String.valueOf(path);
        final File newDirectory = new File(currentPath, newFolder);
        if (!newDirectory.exists()) {
            newDirectory.mkdir();
        }
    }

    public static void commandRm(String deleteObject) {
        final File delete = new File(deleteObject);
        if (delete.exists()) {
            delete.delete();
        }
    }
    
    public static void commandLs(Path path, String param) {
        if (param.equals("-i")) {
            String currentPath = String.valueOf(path);
            File dir = new File(currentPath);
            for (File file : dir.listFiles()) {
                System.out.println(file.getName() + " " +
                        file.length() / 1024 + "Kb" );
            }
        }
    }

    public static void commandLs(Path path) {
        String currentPath = String.valueOf(path);
        File dir = new File(currentPath);
        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
        }
    }

    private static void print(Path dir) {
        System.out.println("");
        System.out.print(dir +":");
    }

    public static void commandCp(String source, String destination) {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);
        try {
            Path bytes = Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Файл уже существует");
        } catch (NoSuchFileException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка обработки");
        }
    }

    public static void commandCp(String parameter, String source, String destination) {
        if (parameter.equals("-f")) {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            try {
                Path bytes = Files.copy(fileSource.toPath(), fileDestination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
            } catch (NoSuchFileException e) {
                System.out.println("Файл не найден");
            } catch (IOException e) {
                System.out.println("Ошибка обработки");
            }
        }
    }

    public static void commandMv(String source, String destination) {
        File fileSource = new File(source);
        File fileDestination = new File(destination);
        try {
            Path bytes = Files.move(fileSource.toPath(), fileDestination.toPath());
        } catch (FileAlreadyExistsException e) {
            System.out.println("Файл уже существует");
        } catch (NoSuchFileException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка обработки");
        }
    }

    public static void commandMv(String parameter, String source, String destination) {
        if (parameter.equals("-f")) {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            try {
                Path bytes = Files.move(fileSource.toPath(), fileDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (NoSuchFileException e) {
                System.out.println("Файл не найден");
            } catch (IOException e) {
                System.out.println("Ошибка обработки");
            }
        }
    }

    static String inputString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    static Path getCurrentDir() {
        return Path.of(System.getProperty("user.dir"));
    }

    public static void printFile(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка вывода помощи");
        }
    }
}