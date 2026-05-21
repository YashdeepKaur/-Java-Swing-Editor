import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class MyEditor implements ActionListener {
    JFrame jf;
    JLabel jl;
    JTextField jtf;
    JTextArea jta, jta1;
    JButton jbcompile, jbrun;
    JScrollPane jsp, jsp1;
    Runtime r;
    String str = "";
    String fname = "";
    String result = "";
    String result1 = "";

    MyEditor() {
        jf = new JFrame("My Editor");
        jf.setLayout(null);
        jl = new JLabel("Enter java Class Name");
        jl.setBounds(20, 20, 130, 25);
        jtf = new JTextField();
        jtf.setBounds(180, 20, 230, 25);
        jta = new JTextArea(50, 50);
        jta.addFocusListener(new MyFocusListener(this));
        jta1 = new JTextArea(50, 50);
        jta.setFont(new Font("Consolas", Font.PLAIN, 15));      // typo: "varinda" → "Consolas" (or any real font)
        jta1.setFont(new Font("Consolas", Font.PLAIN, 15));

        jsp = new JScrollPane(jta);
        jsp1 = new JScrollPane(jta1);
        jsp.setBounds(50, 60, 320, 150);
        jsp1.setBounds(50, 270, 320, 150);
        jf.add(jsp);
        jf.add(jsp1);
        jbcompile = new JButton("Compile");
        jbrun = new JButton("Run");
        jbcompile.setBounds(100, 230, 80, 25);
        jbrun.setBounds(280, 230, 80, 25);
        jf.add(jl);
        jf.add(jtf);
        r = Runtime.getRuntime();
        jf.add(jbcompile);
        jf.add(jbrun);
        jbcompile.addActionListener(this);
        jbrun.addActionListener(this);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(550, 550);
        jf.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbcompile) {
            str = "";
            if (!jtf.getText().equals("")) {
                try {
                    fname = jtf.getText().trim() + ".java";
                    FileWriter fw = new FileWriter(fname);
                    String s1 = jta.getText();
                    PrintWriter pw = new PrintWriter(fw);
                    pw.println(s1);
                    pw.flush();
                    pw.close();

                    // Use JDK 8 path (replace "251" with your actual update number)
                    String javacPath = "C:\\Program Files\\Java\\jdk1.8.0_251\\bin\\javac.exe";
                    Process error = r.exec(new String[] { javacPath, "-d", ".", fname });

                    BufferedReader err = new BufferedReader(new InputStreamReader(error.getErrorStream()));
                    result = "";
                    result1 = "";

                    while (true) {
                        String temp = err.readLine();
                        if (temp != null) {
                            result += temp + "\n";
                        } else break;
                    }

                    if (result.equals("")) {
                        jta1.setText("Compilation successful! " + fname);
                    } else {
                        jta1.setText(result);
                    }
                    err.close();
                } catch (Exception e1) {
                    jta1.setText("Exception during compile: " + e1.toString());
                }
            } else {
                jta1.setText("Please Enter the java program name!");
            }
        } else if (e.getSource() == jbrun) {
            try {
                String fn = jtf.getText().trim();
                String javaPath = "C:\\Program Files\\Java\\jdk1.8.0_251\\bin\\java.exe";
                Process p = r.exec(new String[] { javaPath, "-cp", ".", fn });

                BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader error  = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                result = "";
                result1 = "";

                while (true) {
                    String temp = output.readLine();
                    if (temp != null) {
                        result += temp + "\n";
                    } else break;
                }

                while (true) {
                    String temp = error.readLine();
                    if (temp != null) {
                        result1 += temp + "\n";
                    } else break;
                }

                output.close();
                error.close();

                jta1.setText(result + "\n" + result1);

            } catch (Exception e2) {
                jta1.setText("Exception during run: " + e2.toString());
            }
        }
    }

    public static void main(String arg[]) {
        new MyEditor();
    }
}

class MyFocusListener extends FocusAdapter {
    MyEditor e;

    MyFocusListener(MyEditor e) {
        this.e = e;
    }

    public void focusGained(FocusEvent fe) {
        String str = e.jtf.getText().trim();
        e.jta.setText("public class " + str + "\n{\n    public static void main(String[] s)\n    {\n\n    }\n}");
    }
}