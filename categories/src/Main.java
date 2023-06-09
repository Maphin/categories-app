import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter command: ");
        String command = scanner.nextLine();

        String withoutCommasCommand = command.replace(",", " ");
        String[] commandParts = withoutCommasCommand.split("\\s+");

        if (commandParts.length < 2) {
            System.out.println("Invalid command.");
            return;
        }

        String operation = commandParts[0];
        String categoryName = commandParts[1].replace(":", "_");

        switch (operation) {
            case "categorize" -> {
                if (commandParts.length < 3) {
                    System.out.println("Invalid command.");
                    return;
                }
                System.out.println(Arrays.toString(commandParts));
                String[] fileNames = Arrays.copyOfRange(commandParts, 2, commandParts.length);
                List<String> filePaths = new ArrayList<>();
                System.out.println("Enter file paths (separated by comma or space):");
                String pathsInput = scanner.nextLine();
                String[] paths = pathsInput.split("[,\\s]+");
                filePaths.addAll(Arrays.asList(paths));
                createTxtFile(categoryName, fileNames, filePaths);
                System.out.println("Files categorized successfully.");
            }
            case "group" -> {
                Path categoryDirectoryPath = Paths.get(categoryName);
                if (!Files.exists(categoryDirectoryPath)) {
                    try {
                        Files.createDirectory(categoryDirectoryPath);
                        System.out.println("Category directory created.");
                    } catch (IOException e) {
                        System.out.println("Error creating category directory: " + e.getMessage());
                        return;
                    }
                }

                Path documentsTxtPath = Paths.get(categoryName + ".txt");
                if (!Files.exists(documentsTxtPath)) {
                    System.out.println("Category file doesn't exist. Create category first using 'categorize' command.");
                    return;
                }

                try {
                    List<String> fileLines = Files.readAllLines(documentsTxtPath);
                    List<String> filePaths = fileLines.stream()
                            .filter(line -> !line.startsWith("Category") && !line.startsWith("File Names") && !line.startsWith("File Paths"))
                            .toList();

                    for (String filePath : filePaths) {
                        Path sourcePath = Paths.get(filePath);
                        if (Files.exists(sourcePath) && Files.isRegularFile(sourcePath)) {
                            Path fileDestinationPath = categoryDirectoryPath.resolve(sourcePath.getFileName());
                            try {
                                Files.move(sourcePath, fileDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.out.println("Error moving file: " + e.getMessage());
                            }
                        }
                    }

                    Path txtFileDestinationPath = categoryDirectoryPath.resolve(documentsTxtPath.getFileName());

                    try {
                        Files.move(documentsTxtPath, txtFileDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("Error moving text file: " + e.getMessage());
                    }

                    System.out.println("Files grouped successfully.");
                } catch (IOException e) {
                    System.out.println("Error reading category file: " + e.getMessage());
                }

            }
            case "ungroup" -> {
                Path categoryDirectoryPath = Paths.get(categoryName);
                if (!Files.exists(categoryDirectoryPath) || !Files.isDirectory(categoryDirectoryPath)) {
                    System.out.println("Category directory doesn't exist.");
                    return;
                }

                Path documentsTxtPath = categoryDirectoryPath.resolve(categoryName + ".txt");
                if (!Files.exists(documentsTxtPath) || !Files.isRegularFile(documentsTxtPath)) {
                    System.out.println("Category file doesn't exist in the category directory.");
                    return;
                }

                try {
                    List<String> fileLines = Files.readAllLines(documentsTxtPath);
                    List<String> filePaths = fileLines.stream()
                            .filter(line -> !line.startsWith("Category") && !line.startsWith("File Names") && !line.startsWith("File Paths"))
                            .toList();

                    for (String filePath : filePaths) {
                        Path sourcePath = categoryDirectoryPath.resolve(filePath);
                        if (Files.exists(sourcePath) && Files.isRegularFile(sourcePath)) {
                            Path fileDestinationPath = Paths.get(filePath);
                            try {
                                Files.move(sourcePath, fileDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                System.out.println("Error moving file: " + e.getMessage());
                            }
                        }
                    }

                    Path txtFileDestinationPath = Paths.get(categoryName + ".txt");

                    try {
                        Files.move(documentsTxtPath, txtFileDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.out.println("Error moving text file: " + e.getMessage());
                    }

                    Files.delete(categoryDirectoryPath); // Delete the category directory

                    System.out.println("Files ungrouped successfully.");
                } catch (IOException e) {
                    System.out.println("Error reading category file: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid command.");
        }
    }

    private static void createTxtFile(String categoryName, String[] fileNames, List<String> filePaths) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(categoryName + ".txt"))) {
            writer.write("Category " + categoryName);
            writer.newLine();

            writer.write("File Names");
            writer.newLine();
            for (String fileName : fileNames) {
                writer.write(fileName);
                writer.newLine();
            }

            writer.write("File Paths");
            writer.newLine();
            for (String filePath : filePaths) {
                writer.write(filePath);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error creating text file: " + e.getMessage());
        }
    }

}
