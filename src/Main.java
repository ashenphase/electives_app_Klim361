public class Main {
    public static void main(String[] args) {
        System.out.println("Тестирование подключения к базе данных");

        DBConnection dbInstance = DBConnection.getInstance();

        if (dbInstance.getConnection() != null) {
            System.out.println("\nJava успешно связалась с MySQL.");

            dbInstance.closeConnection();
        }
        else {
            System.err.println("\nНе удалось получить активное соединение.");
        }
    }
}
