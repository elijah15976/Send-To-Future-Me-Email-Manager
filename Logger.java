import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;

class Logger{
    private final String info = " \u001b[32m[Info]\u001b[0m ";
    private final String warn = " \u001b[33m[Warn]\u001b[0m ";
    private final String error = " \u001b[31m[Error]\u001b[0m ";
    private final String crit = " \u001b[47m[Critical]\u001b[0m ";

    public Logger(){
        if(System.getProperty("os.name").startsWith("Windows")){
            // Set output mode to handle virtual terminal sequences
            Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
            DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
            HANDLE hOut = (HANDLE)GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

            DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
            Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
            GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

            int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
            DWORD dwMode = p_dwMode.getValue();
            dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
            Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
            SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
        }
    }
    
    public void dispMessage(final String message, final String type) {
        String stat = " ";
        String date = java.time.LocalDate.now().toString();
        String time = java.time.LocalTime.now().toString();
        if (type.equalsIgnoreCase("info")) {
            stat = info;
        }
        else if (type.equalsIgnoreCase("warn")) {
            stat = warn;
        }
        else if (type.equalsIgnoreCase("error")) {
            stat = error;
        }
        else if (type.equalsIgnoreCase("crit")) {
            stat = crit;
        }
        System.out.println(date + " " + time.substring(0, time.indexOf(".")) + stat + message);
    }
}