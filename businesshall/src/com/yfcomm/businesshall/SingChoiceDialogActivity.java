package com.yfcomm.businesshall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SingChoiceDialogActivity extends Activity {  
    
    private final int SING_CHOICE_DIALOG = 1;  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.single_choice_dialog_layout);  
          
        Button button = (Button) findViewById(R.id.button);  
        View.OnClickListener listener = new View.OnClickListener() {  
              
            @SuppressWarnings("deprecation")
			@Override  
            public void onClick(View view) {  
                showDialog(SING_CHOICE_DIALOG);  
            }  
        };  
        button.setOnClickListener(listener);  
    }  
      
    @Override  
    protected Dialog onCreateDialog(int id) {  
        Dialog dialog = null;  
        switch(id) {  
            case SING_CHOICE_DIALOG:  
                Builder builder = new AlertDialog.Builder(this);  
                builder.setIcon(R.drawable.prompt);  
                builder.setTitle("体育爱好");  
                final ChoiceOnClickListener choiceListener =   
                    new ChoiceOnClickListener();  
                builder.setSingleChoiceItems(R.array.hobby, 0, choiceListener);  
                  
                DialogInterface.OnClickListener btnListener =   
                    new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialogInterface, int which) {  
                              
                            EditText editText = (EditText) findViewById(R.id.editText);  
                            int choiceWhich = choiceListener.getWhich();  
                            String hobbyStr =   
                                getResources().getStringArray(R.array.hobby)[choiceWhich];  
                            editText.setText("你选择了  " + hobbyStr);  
                        }  
                    };  
                builder.setPositiveButton("确定", btnListener);  
                dialog = builder.create();  
                break;  
        }  
        return dialog;  
    }  
      
    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {  
  
        private int which = 0;  
        @Override  
        public void onClick(DialogInterface dialogInterface, int which) {  
            this.which = which;  
        }  
          
        public int getWhich() {  
            return which;  
        }  
    }  
}  
