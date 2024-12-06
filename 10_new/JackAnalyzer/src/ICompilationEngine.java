public interface ICompilationEngine {

    void compileClass();

    void compileClassVarDec();

    void compileVarDec();

    String compileType();

    void compileSubroutine();

    void compileParameterList();

    void compileSubroutineBody();

    void compileStatements();

    void compileLet();

    void compileDo();

    int compileExpressionList();

    void compileWhile();

    void compileReturn();

    void compileIf();
    void compileExpression();
    void compileTerm();
}
