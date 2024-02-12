import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class QuizCentre {
static boolean quizState = false;
static File quizFile = null;
static int numQuestions = 0;
static int totalQuizTimeInSeconds = 0;
static int numUsers = 0;
static String filePath = null;
static int fileChoice = 0;
public static void main(String[] args) throws Exception{
    Scanner scanner = new Scanner(System.in);
    
  //  startQuiz();
    System.out.println("---------------\"WELCOME TO THE QUIZ_CENTRE\"---------------");
    System.out.println("----------USE '3' At entering choice time to EXIT-----------");

    while(true) {
    System.out.println("\nAs what ROLE, you want to continue ?(ADMIN OR CANDIDATE)\n"+//
                        "[TYPE '1' FOR ADMIN AND '2' FOR ALL CANDIDATES]");
    int choice = 0;
     try {
        choice = scanner.nextInt();
       // break;
        } catch (InputMismatchException e) {
         System.out.println("Invalid input. Please enter a valid integer choice.");
       scanner.nextLine(); // Consume the invalid input
    }

    switch(choice){

        case 1 :
            while(true) {
             System.out.print("Enter ADMIN password :");
             String psw = scanner.next();
             System.out.println();
                if(psw.equalsIgnoreCase("IAMADMIN")) {
                    break;
                } else if(psw.equalsIgnoreCase("3")) {
                    System.exit(0);
                } else {
                    System.out.println("Wrong password or role.");
                }
            }
            boolean flag = true;
            while(flag) {
             System.out.println("Choose an option for \"QUIZ-TYPE\":");
             System.out.println("1. Take a quiz from the inbuilt quizzes.");
             System.out.println("2. Take a quiz from your own file.(By entering 'FILE-PATH')");
            int choice2 = 0;

            try {
              choice2 = scanner.nextInt();
           //   break;
              } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a valid integer choice.");
             scanner.nextLine(); // Consume the invalid input
             }                 
             switch (choice2) {
                case 1:
                    takeInbuiltQuiz(scanner);
                    if(quizFile == null) {
                        System.out.println("Try again.");
                    } else {
                    fileChoice = 1;
                    flag = false;
                    }
                    break;
                 case 2:
                    takeCustomQuiz(scanner);
                    if(quizFile == null) {
                        System.out.println("Try again.");
                    } else {
                    fileChoice = 2;
                    flag = false;
                    }
                    break;
                 case 3:
                    System.exit(0);
                 default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
             }
            }

            System.out.print("Enter the total time for the quiz (in seconds): ");
            totalQuizTimeInSeconds = scanner.nextInt();

            System.out.print("Enter the number of users you want to give the quiz: ");
            numUsers = scanner.nextInt();

            quizState = true;
            break;

        case 2 :
             if(quizState) {
                System.out.println("Quiz is started for Candidates !!");
                startQuiz();
                System.exit(0);
             } else {
                System.out.println("Quiz is not created yet,Sorry !");
             }            
             break;
        case 3 :
             System.exit(0);

        default :
             System.out.println("Invalid I/P, Try again.");
             break;
        } 
    }
}

public static void startQuiz() throws IOException {
    Scanner scanner = new Scanner(System.in);
    List<User> users = new ArrayList<>();

    for (int i = 1; i <= numUsers; i++) {

        System.out.println("User " + i + ":");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your ID number: ");
        String ID = scanner.nextLine();
        
        int score = 0; // Initialize the user's score
        long totalElapsedTime = 0; // Initialize the total time taken by the user

        // Store user information in a text file
        try {
            FileWriter fileWriter = new FileWriter(name + ".txt", true);
            fileWriter.write("\nName: " + name + "\n");
            fileWriter.write("ID Number: " + ID + "\n\n");

            List<QuestionAnswer> availableQuestions = readAvailableQuestions(quizFile);
            List<QuestionAnswer> selectedQuestions = selectRandomQuestions(availableQuestions, numQuestions);

            int questionNumber = 0;
            //long timePerQuestion = totalQuizTimeInSeconds * 1000 / numQuestions; // Convert to milliseconds
            for (QuestionAnswer qa : selectedQuestions) {
                System.out.print("[Total time remaining " +(totalQuizTimeInSeconds - (totalElapsedTime/1000)) + " seconds] \n");
                questionNumber++;
                fileWriter.write("Question " + questionNumber + ": " + qa.getQuestion() + "\n");
                System.out.println("Question " + questionNumber + ": " + qa.getQuestion());

                long startTime = System.currentTimeMillis();
                String userAnswer = scanner.nextLine(); // Read the entire line for the user's answer
                long endTime = System.currentTimeMillis();
                
                long elapsedTime = endTime - startTime;
                totalElapsedTime += elapsedTime;

               // fileWriter.write("Time Taken for Question " + questionNumber + ": " + elapsedTime + " ms\n");
                fileWriter.write("Your Answer for Question " + questionNumber + ": " + userAnswer + "\n");

                // Check if the user's answer is correct
                String correctAnswer = qa.getAnswer();
                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                    fileWriter.write("Correct: Yes\n");
                    score++;
                } else {
                    fileWriter.write("Correct: No (Correct Ans : " + correctAnswer + ")\n");
                }
                
                if ((totalElapsedTime/1000) > totalQuizTimeInSeconds) {
                    System.out.println("TIME's UP FOR QUIZ ");
                    break;
                }
            }

            // Write the user's total time and score to the file
            fileWriter.write("Total Time: " + (totalElapsedTime/1000) + " sec\n");
            fileWriter.write("Score: " + score + "\n");
            if(score >0){
            users.add(new User(name, score));
            }

            System.out.println("Quiz for " + name + " has been done.\n Your score: " + score);
            System.out.println("Total Time Taken: " + (totalElapsedTime/1000) + " sec");

            fileWriter.close();
        } catch (Exception e) {
            System.err.println("Error while running quiz: " + e.getMessage());
        }
    }

    if(!(users.isEmpty())) {
    //User winner = Collections.max(users, Comparator.comparing(User::getScore));
    Collections.sort(users, Comparator.comparing(User::getScore).reversed());
    int winIndex = 0;
    User winner = users.get(winIndex);

    System.out.println("Winner: " + winner.getName() + " with a score of " + winner.getScore()+" out of "+numQuestions+" .");
    winIndex++;
  
    while(winIndex < numUsers && users.get(winIndex).getScore() == users.get(winIndex-1).getScore()) {
        winner = users.get(winIndex);
        System.out.println("Winner: " + winner.getName() + " with a score of " + winner.getScore()+" out of "+numQuestions+" ."); 
        winIndex++;        
    }
} else {
    System.out.println("No one has scored more than 0.");
}
    scanner.close();
}

public static int CountQue(String filename) throws IOException {
    int QueCounter = 0;
    int QA = 0;
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;

    while ((line = br.readLine()) != null) {
        if (QA == 0 && line.startsWith("Q: ")) {
            if (line.length() > 3) {
                QueCounter++;
                QA = 1;
            } else {
                return -1;
            }
        } else if (QA == 1 && line.startsWith("A: ")) {
            if (line.length() > 3) {
                QA = 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    br.close();
    return QueCounter;
}

private static void takeInbuiltQuiz(Scanner scanner) throws Exception {
    File[] quizFiles = new File(".").listFiles((dir, name) -> name.startsWith("GK_quiz"));
    if(!(quizFiles.length==0)){
    File randomQuizFile = quizFiles[(int) (Math.random() * quizFiles.length)];
    System.out.println("Taking quiz from: " + randomQuizFile.getName());
    numQuestions = CountQue(randomQuizFile.getAbsolutePath());
        quizFile = randomQuizFile;
    }
    else{
        System.out.println("At this moment there is no inbuilt quiz for users.");
    }
}

private static void takeCustomQuiz(Scanner scanner) throws Exception{
    System.out.println("Enter the file path of the quiz:");
    filePath = scanner.next();

    File customQuizFile = new File(filePath);
    if (!customQuizFile.exists()) {
        System.out.println("File not found. Please check the file path and try again.");
        return ;
    }

    numQuestions = CountQue(filePath);

    if (numQuestions == -1) {
        System.out.println("Found Wrong Format.");
        return;
    } else {
        System.out.println("Total Questions are " + numQuestions + ".");
    }

    System.out.println("Taking quiz from: " + customQuizFile.getName());
    quizFile = new File(customQuizFile.getName());

    // Source file path
    Path sourcePath = Path.of(filePath);
    
    // Destination file path in the current directory
    Path destinationPath = Path.of(customQuizFile.getName());
    

    // Copying the file to current directory
    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
 }


private static List<QuestionAnswer> readAvailableQuestions(File filename) throws IOException {
    List<QuestionAnswer> questions = new ArrayList<>();
    BufferedReader br = new BufferedReader(new FileReader(filename));
    String line;
    StringBuilder currentQuestion = new StringBuilder();
    String currentAnswer = null;
    while ((line = br.readLine()) != null) {
        if (line.startsWith("Q: ")) {
            // Store the previous question and answer
            if (currentQuestion.length() > 0 && currentAnswer != null) {
                questions.add(new QuestionAnswer(currentQuestion.toString(), currentAnswer));
            }
            currentQuestion = new StringBuilder(line.substring(3));
        } else if (line.startsWith("A: ")) {
            currentAnswer = line.substring(3);
        }
    }
    // Add the last question and answer
    if (currentQuestion.length() > 0 && currentAnswer != null) {
        questions.add(new QuestionAnswer(currentQuestion.toString(), currentAnswer));
    }
    return questions;
}

private static List<QuestionAnswer> selectRandomQuestions(List<QuestionAnswer> questions, int numQuestions) {
    List<QuestionAnswer> selectedQuestions = new ArrayList<>();
    Random random = new Random();
    int maxIndex = questions.size() - 1;
    for (int i = 0; i < numQuestions; i++) {
        int randomIndex = random.nextInt(maxIndex + 1);
        selectedQuestions.add(questions.get(randomIndex));
        questions.remove(randomIndex);
        maxIndex--;
    }
    return selectedQuestions;
}

private static class QuestionAnswer {
    private String question;
    private String answer;

    public QuestionAnswer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

 private static class User {
    private String name;
    private int score;

    public User(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
}
}