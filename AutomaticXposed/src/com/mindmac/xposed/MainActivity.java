package com.mindmac.xposed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	
	private static final String SELF_PACKAGE_NAME = "com.mindmac.xposed";
	private static final String XPOSED_PACKAGE_NAME = "de.robv.android.xposed.installer";
	private static final String XPOSED_MODULE = "xposed_module";
	private static final String XPOSED_FILE = "XposedInstaller.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		// Install xposed and enable xposed module
		installAndEnableXposed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	 private void installAndEnableXposed(){
	        (new Thread() {
	            @Override
	            public void run() {
		        	// Check and Install Xposed
	            	if(!isXposedInstalled()){
	            		storeXposedApk();
	            		installXposed();
	            	}
	            	
	            	try {
						Thread.sleep(30 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            	if(isXposedInstalled())
	            		enableXposedModule();	
		            	
	            }
	        }).start();
	    }
	 
	 private boolean isXposedInstalled(){
	    	boolean installed = false;
	    	PackageManager packageManager = getPackageManager();
	    	List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
	    	for(PackageInfo packageInfo : packageInfos){
	    		if(packageInfo.packageName.equals(XPOSED_PACKAGE_NAME)){
	    			installed = true;
	    			break;
	    		}
	    	}
	    	return installed;
	    }
	    
	    private void storeXposedApk(){
	    	AssetManager assetManager = getAssets();
	    	InputStream inputStream = null;
	    	FileOutputStream fileOutputStream = null;
	    	byte[] fileBytes = new byte[1024];
	    	try {
	    		inputStream = assetManager.open(XPOSED_FILE);
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File sdCardDir = Environment.getExternalStorageDirectory();
					File saveFile = new File(sdCardDir, XPOSED_FILE);
					fileOutputStream = new FileOutputStream(saveFile);
					while(inputStream.read(fileBytes) != -1)
						fileOutputStream.write(fileBytes);
				}
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}finally{
					try {
						if(inputStream != null)
							inputStream.close();
						if(fileOutputStream != null)
							fileOutputStream.close();
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
			}
	    	
	    }
	    
	    private void installXposed() {
	        new Thread() {
	            public void run() {
	            	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	        			File sdCardDir = Environment.getExternalStorageDirectory();
	        			File saveFile = new File(sdCardDir, XPOSED_FILE);
	        			if(!saveFile.exists())
	        				return;
	        			
		                Process process = null;
		                OutputStream out = null;
		                try {
		                    process = Runtime.getRuntime().exec("su");
		                    out = process.getOutputStream();
		                    out.write(("pm install -r " + saveFile.getAbsolutePath() + "\n").getBytes());
		                } catch (IOException ex) {
		                    ex.printStackTrace();
		                } catch (Exception ex) {
		                   ex.printStackTrace();
		                } finally {
		                    try {
		                        out.flush();
		                        process.waitFor();
		                        out.close();
		                        System.out.println("Install success");
		                    } catch (Exception ex) {
		                        System.out.println("Install failed");
		                        System.out.println(ex.getMessage());
		                    }
		                }
		            }
	            }
	        }.start();
	    }
	    
	    private void enableXposedModule(){
			 Intent intent = new Intent();
		     intent.setClassName("de.robv.android.xposed.installer", 
		    		 "de.robv.android.xposed.installer.InstallService");
		     intent.putExtra(XPOSED_MODULE, SELF_PACKAGE_NAME);
		     System.out.println("Enable module");
		     startService(intent);
	    }

}
