package ipp.estg.lei.cmu.trabalhopratico.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ipp.estg.lei.cmu.trabalhopratico.game.classificacoes.database.ClassiDatabase;
import ipp.estg.lei.cmu.trabalhopratico.game.classificacoes.models.Classificacao;
import ipp.estg.lei.cmu.trabalhopratico.R;


public class GamePlayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    TextView textTXT;
    TextView questioncTXT;
    EditText userInput;
    EditText tipTXT;
    Button checkButton;
    Button tipsButton;
    private int counter = 0;
    private int[] attempts = new int[12];
    ClassiDatabase classiDb;
    private String question;


    public GamePlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = questionGeneration();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mViewContent = inflater.inflate(R.layout.fragment_game_play, container, false);
        textTXT= mViewContent.findViewById(R.id.questionTXT);
        textTXT.setText(question);
        userInput = mViewContent.findViewById(R.id.replyText);
        tipTXT = mViewContent.findViewById(R.id.gameTipsTxt);
        questioncTXT = mViewContent.findViewById(R.id.counterTXT);
        checkButton = mViewContent.findViewById(R.id.checkButton);
        tipsButton = mViewContent.findViewById(R.id.tipButton);
        classiDb = ClassiDatabase.getDatabase(getActivity().getApplicationContext());

        for(int ix=0; ix<12; ix++)
            attempts[ix]=0; //Inicializa o array de tentativas
        //final String[] q_partstip = textTXT.getText().toString().substring(0, textTXT.getText().toString().indexOf("=")).split(" ");
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int first = Integer.parseInt(q_partstip[0]), second = Integer.parseInt(q_partstip[2]);
                     tipTXT.setText(generateTip(first, second, q_partstip[1]));
            }
        }, 15000); */

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userInput.getText().toString().equals("") && !userInput.getText().toString().equals("-")) {
                        String question = textTXT.getText().toString();
                        String answer = userInput.getText().toString();
                        String[] q_parts = question.substring(0, question.indexOf("=")).split(" ");
                        int first = Integer.parseInt(q_parts[0]), second = Integer.parseInt(q_parts[2]),
                                ans = Integer.parseInt(answer);
                        String op = q_parts[1];
                        if (checkResults(first, second, op, ans) && counter < 12) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                            alertDialog.setTitle("Correto!");
                            alertDialog.setMessage("O Resultado está correto!");
                            alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    textTXT.setText(questionGeneration());
                                    questioncTXT.setText(" Questões respondidas:" + counter);
                                }
                            });
                            alertDialog.show();
                        } else if (counter < 12) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                            alertDialog.setTitle("Incorreto!");
                            alertDialog.setMessage("O Resultado está incorreto!");
                            alertDialog.show();  //<-- See This!
                        }

                        if (counter == 12) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                            alertDialog.setTitle("Jogo terminado");
                            alertDialog.setMessage("Terminou o jogo, respondendo às 12 questões corretamente! Pontuação alcançada: " + calculateScore());
                            alertDialog.setButton("Voltar ao ínicio", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                public void onClick(DialogInterface dialog, int which) {
                                    // recordClassification(calculateScore());
                                    DataAsyncTask dataAsyncTask = new DataAsyncTask(classiDb);
                                    Classificacao classificacao = new Classificacao("user", calculateScore());
                                    dataAsyncTask.execute(classificacao);
                                    FragmentManager fm = getFragmentManager();
                                    if (fm.getBackStackEntryCount() > 0) {
                                        fm.popBackStack();
                                    }
                                }
                            });
                            alertDialog.show();
                        }
                    }
                }
        });

        tipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = textTXT.getText().toString();
                String[] q_parts = question.substring(0, question.indexOf("=")).split(" ");
                int first = Integer.parseInt(q_parts[0]), second = Integer.parseInt(q_parts[2]);
                String op = q_parts[1];
                String tip = generateTip(first, second, op);
                tipTXT.setText(tip);
            }
        });

        return mViewContent;
    }

    public String questionGeneration(){
        if(counter<3){
            String operator = "+";
            int random = (int) Math.floor((Math.random() * 2));
            if (random == 0) {
                operator = "+";
            } else if (random == 1) {
                operator = "-";
            }
            return (int) (Math.floor(Math.random() * 16)) + " " + operator + " " +
                    (int) (Math.floor(Math.random() * 16)) + " = x";
        }else if(counter<6){
            String operator = "*";
            int random = (int) Math.floor((Math.random() * 2));
            if (random == 0) {
                operator = "*";
            } else if (random == 1) {
                operator = "/";
            }
            int n1, n2;
            n1 = (int) (Math.floor(Math.random() * 11));
            n2 = (int) (Math.floor(Math.random() * 11));
            if(operator.equals("/"))
                while(n2==0 || n1%n2!=0){
                    n1 = (int) (Math.floor(Math.random() * 11));
                    n2 = (int) (Math.floor(Math.random() * 11));
                }
            return n1 + " " + operator + " " +
                    n2 + " = x";
        }else if(counter<12){
            String operator = "+";
            int random = (int) Math.floor((Math.random() * 4));
            if (random == 0) {
                operator = "+";
            } else if (random == 1) {
                operator = "-";
            } else if (random == 2) {
                operator = "*";
            } else if (random == 3) {
                operator = "/";
            }
            int n1, n2;
            n1 = (int) (Math.floor(Math.random() * 16));
            n2 = (int) (Math.floor(Math.random() * 15)+1);
            if(operator.equals("/")) {
                    n2 = (int) (Math.floor(Math.random() * 16));
                        while (n1 % n2 != 0) {
                            n2 = (int) (Math.floor(Math.random() * 15)+1);
                        }
            }
            return  n1+ " " + operator + " " +
                     n2+ " = x";
        }
        return  "";
    }

    public boolean checkResults(int n1, int n2, String op, int ans){
        attempts[counter]++;
        if (op.equals("+")) {
            if (n1 + n2 == ans) {
                counter++;
                return true;
            }
        } else if (op.equals("-")) {
            if (n1 - n2 == ans) {
                counter++;
                return true;
            }
        } else if (op.equals("*")) {
            if (n1 * n2 == ans) {
                counter++;
                return true;
            }
        } else if (op.equals("/")) {
            do {
                if (n1 / n2 == ans) {
                    counter++;
                    return true;
                }
            }while(n1%n2!=0);
        }
        return false;
    }

    private int calculateScore(){
        int total = 120;
        int lostPoints;
        for(int ix=0; ix<12; ix++){
            if(attempts[ix]>9)
                lostPoints=9;
            else
                lostPoints=attempts[ix]-1;
            total = total - lostPoints;
        }
        return total;
    }

    private String generateTip(int n1, int n2, String op){

        int ans=0;
        if (op.equals("+")) {
            ans = n1 + n2 ;
        } else if (op.equals("-")) {
            ans = n1-n2;
        } else if (op.equals("*")) {
            ans = n1*n2;
        } else if (op.equals("/")) {
            ans= n1/n2;
        }
        int temp=ans;
        while (ans >= 10)
            ans/= 10;

        if(temp>10) {
            return "Uma ajuda! \n" +
                    "O primeiro dígito do número é: " + ans;
        }else{
            return "Esta é fácil, vai conseguir!";
        }
    }

    private class DataAsyncTask extends AsyncTask<Classificacao, Void, Void>{

        ClassiDatabase classiDatabase;
        /*ClassificacaoDAO classificacaoDAO;

        public void setDAO(ClassificacaoDAO classificacaoDAO){
            this.classificacaoDAO=classificacaoDAO;
        } */

        public DataAsyncTask (ClassiDatabase classiDatabase){
            this.classiDatabase=classiDatabase;
        }

        @Override
        protected Void doInBackground(final Classificacao... classificacaos) {
            ClassiDatabase.databaseWriteExecutor.execute(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    classiDatabase.getClassiDao().insertClassificacao(classificacaos);
                }
            });

            return null;
        }
    }

}
