package exemplo01;

import  java.sql.*;
import org.sqlite.SQLiteConfig;


public class ExemploMuitoSimples {
    private String DB_URI = "jdbc:sqlite:lab01.sqlite";
    private SQLiteConfig sqLiteConfig;
    private final String DIVISOR = "---------------------------------------------------------------------------------\n";

    // Construtor com parâmetros
    public ExemploMuitoSimples(String dB_URI) {
        this();
        DB_URI = dB_URI;
    }

    // Construtor sem parâmetros
    public ExemploMuitoSimples() {
        this.sqLiteConfig = new SQLiteConfig();
        // Pro SQLite tem que ativar true para chaves estrangeiras, no MySQL não
        sqLiteConfig.enforceForeignKeys(true); // ativa restrição de chaves estrangeiras
    }

    // Méthodo cadastra pessoas: Aqui está concatenando os valores direto na string SQL, o que não é recomendado (você vai
    //aprender a usar PreparedStatement depois).
    public int cadastrarPessoa(String nome, double peso, int altura, String email) throws SQLException {
        int resultado = -1;
        // Tenta conectar com o endereço e configura
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement stmt = conexao.createStatement()) {
            String sql = "INSERT INTO Pessoa (nome, peso, altura, email) VALUES ('" + nome + "'," + peso + "," + altura + ",'" + email + "')";
            resultado = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar pessoa", e);
        }

        // stmt.executeUpdate(sql);
        return resultado;
    }

    // Méthodo altera pessoa
    public int alterarDadosPessoa(int idPessoa, String nome, double peso, int altura, String email) throws
            SQLException {
        int resultado = -1;
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement stmt = conexao.createStatement()) {
            String sql = "UPDATE Pessoa SET nome = '" + nome + "', peso=" + peso + ", altura=" + altura + ", email = '" + email + "' WHERE idPessoa=" + idPessoa;
            resultado = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Erro ao alterar dados de pessoas", e);
        }
        return resultado;
    }

    // Méthodo exclui pessoa
    public int excluirPessoa(int idPessoa) throws SQLException {
        int resultado = -1;
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement stmt = conexao.createStatement()) {
            String sql = "DELETE FROM Pessoa WHERE idPessoa = " + idPessoa;
            resultado = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir pessoas", e);
        }
        return resultado;
    }

    // Méthodo que lista todas as pessoas
    public String listarRegistros() throws SQLException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM Pessoa";
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                sb.append("\nNenhuma pessoa cadastrada no banco\n");
            } else {
                sb.append(DIVISOR);
                sb.append(String.format("|%-5s|%-25s|%-10s|%-10s|%-25s|\n", "ID", "Nome", "Peso", "Altura", "Email"));
                        sb.append(DIVISOR);
                do {
                    sb.append(String.format("|%-5d|%-25s|%-10.2f|%-10d|%-25s|\n",
                            rs.getInt("idPessoa"),
                            rs.getString("Nome"),
                            rs.getDouble("peso"),
                            rs.getInt("altura"),
                            rs.getString("email")));
                } while (rs.next());
                sb.append(DIVISOR);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar todas pessoas", e);
        }
        return sb.toString();
    }

    // Méthodo que busca pessoa pelo email pessoal
    public String listarDadosPessoa(String emailPessoa) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM Pessoa WHERE Email = '" + emailPessoa + "'";
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                sb.append("\nNenhuma pessoa cadastrada possui o email informado\n");
            } else {
                do {
                    sb.append(String.format("%d|%s|%.2f|%d|%s\n",
                            rs.getInt("idPessoa"),
                            rs.getString("Nome"),
                            rs.getDouble("peso"),
                            rs.getInt("altura"),
                            rs.getString("email")));
                } while (rs.next());
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar dados de pessoas", e);
        }
        return sb.toString();
    }

    // Méthodo que cria o banco de dados do zero. Apaga a tabela Pessoa se existir e cria uma nova com um registro inicial.
    public boolean criaBancoDeDados() throws Exception {
        try (Connection conexao = DriverManager.getConnection(DB_URI, this.sqLiteConfig.toProperties());
             Statement statement = conexao.createStatement();) {
            statement.executeUpdate("drop table if exists Pessoa");
            statement.executeUpdate("create table Pessoa ( idPessoa INTEGER not null\n" +
                    " primary key AUTOINCREMENT,\n" +
                    " Nome TEXT not null,\n" +
                    " Peso REAL,\n" +
                    " Altura INTEGER,\n" +
                    " Email Text)");
            statement.executeUpdate("INSERT INTO Pessoa (Nome, Peso, Altura, Email) " +
                    "VALUES ('Aluno Teste', 85.2, 180, 'aluno@teste.com.br')");
        } catch (Exception e) {
            throw new Exception("Erro ao criar tabelas", e);
        }
        return true;
    }
}
