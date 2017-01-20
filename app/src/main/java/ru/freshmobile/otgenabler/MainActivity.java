package ru.freshmobile.otgenabler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private Process suProcess;
    private Switch otgSwitch;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        otgSwitch = (Switch) findViewById(R.id.switch1);
        getRoot();
        String result = execCommand("id\n", true);
        if (result == null || !result.contains("uid=0")) {
            Toast.makeText(this, "Error, app requires root access", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getState();
    }

    public void switchOtgClick(View v) {
        state = 1 - state;
        String cmd = "echo \"" + state + "\" >> /sys/kernel/debug/regulator/8226_smbbp_otg/enable\n";
        execCommand(cmd, false);
    }

    private void getState() {
        String cmd = "cat /sys/kernel/debug/regulator/8226_smbbp_otg/enable\n";
        state = Integer.parseInt(execCommand(cmd, true));
        boolean isEnabled = (state == 1);
        otgSwitch.setChecked(isEnabled);
    }

    private void getRoot() {
        try {
            suProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
        }
    }

    private String execCommand(String cmd, boolean isOutputNeeded) {
        String result = null;
        try {
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            BufferedReader osRes = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
            if (null != os && null != osRes) {
                os.writeBytes(cmd);
                os.flush();
                if (isOutputNeeded) {
                    result = osRes.readLine();
                }
            }
        } catch (IOException ioe) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch(NullPointerException npe){
            Toast.makeText(this, "Error, app requires root access", Toast.LENGTH_LONG).show();
            finish();
        }
        return result;
    }

}
