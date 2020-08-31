#include <vector>
#include <string>
#include <fstream>
#include <iostream> 

enum Token_Type {
    IDENT,
    LET,
    EQUALS,
    ARROW,
    LPAREN,
    RPAREN,
    RETURN,
    CONSTRAIN,
    SEMICOLON,
};

class Token {
    public:
        virtual std::string string() = 0;
        virtual bool matches(Token_Type t) = 0;
};

class Ident : Token {
    public:
      std::string value;

    Ident(std::string input) {
        value = input;
    }

    std::string string() {
      return value;
    }

    bool matches(Token_Type t) {
        return t == Token_Type::IDENT;
    }
};

class Context{};

struct Geometry{};

class Stmt {
    public:
        virtual Geometry interpret(Context ctx);
};

class LetStmt : Stmt{};
class Constraint : Stmt{};
class Expr {};
class Literal : Expr{};
class Call : Expr{};

// input chars -> token stream -> stmt list
class Parser {
  public: 
  void tokenize(std::ifstream input) {
    std::istreambuf_iterator<char> begin(input);
    return;
  }

  void parse() {
    return;
  }
 
  std::vector<Token> token_stream; 
  std::vector<Stmt> program;
};