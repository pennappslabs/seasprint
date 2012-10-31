package com.engineering.printer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.engineering.printer.TextWithIconAdapter.ItemWithIcon;
import com.engineering.printer.TextWithIconAdapter.SimpleItemWithIcon;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import io.filepicker.FilePicker;
import io.filepicker.FilePickerAPI;

/**
 * 
 * Welcome screen of the app. Allows the user to choose the location
 * where the documents to print are stored.
 * @author Jun Ying
 *
 */
public class SourceSelectScreen extends Activity implements OnItemClickListener {
	private static final String FILEPICKER_KEY = "AC0gvvjhEQzK-cP5HE5cLz";
	private final int REQUEST_PRINT = 1;
	private final int REQUEST_SDCARD = 2;
	private final int REQUEST_ENIAC = 3;
	private final int REQUEST_THIRDPARTY = 4;

	private List<String> m3rdPartyPackageName = new ArrayList<String>();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sourceselect);
		
		//Set FilePicker key
		FilePickerAPI.setKey(FILEPICKER_KEY);

		//Prepare the file source list items
		final ListView lvFileSource = (ListView) findViewById(R.id.lvFileSource);
		lvFileSource.setOnItemClickListener(this);

		List<ItemWithIcon> entries = new ArrayList<ItemWithIcon>();
		//Feature lists
		entries.add(new SimpleItemWithIcon("SD Card", R.drawable.sdcard_icon,
				null));
		entries.add(new SimpleItemWithIcon("Remote ENIAC Server",
				R.drawable.eniac_icon, null));
		entries.add(new SimpleItemWithIcon("Other Cloud Services",
				R.drawable.filepicker_icon, null));
		//List third party file browser
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				intent, PackageManager.GET_RESOLVED_FILTER);

		for (ResolveInfo info : resInfo) {
			ActivityInfo aInfo = info.activityInfo;
			if (aInfo != null) {
				ApplicationInfo appInfo = aInfo.applicationInfo;
				m3rdPartyPackageName.add(appInfo.packageName);
				entries.add(new SimpleItemWithIcon(appInfo.loadLabel(getPackageManager()), null, 
						appInfo.loadIcon(getPackageManager())));

			}

		}

		//Put them on the listview
		TextWithIconAdapter adapter = new TextWithIconAdapter(this,
				R.layout.filesource_item, entries);

		lvFileSource.setAdapter(adapter);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PRINT)
			;// Do nothing
		else if (requestCode == FilePickerAPI.REQUEST_CODE_GETFILE
				|| requestCode == REQUEST_SDCARD 
				|| requestCode == REQUEST_THIRDPARTY) {

			// Result was cancelled by the user or there was an error
			if (resultCode != RESULT_OK)
				return;
			//Read uri
			Uri uri = null;
			if (requestCode == REQUEST_SDCARD) {
				String fileChosen = data.getExtras().getString(
						"com.engineering.printer.fileChosen");
				uri = Uri.fromFile(new File(fileChosen));
			} else if (requestCode == REQUEST_THIRDPARTY
					|| requestCode == FilePickerAPI.REQUEST_CODE_GETFILE)
				uri = data.getData();
			
			if(uri!=null)
			{
				//Send Intent to PrinterSelectScreen
				Intent printIntent = new Intent(this, PrinterSelectScreen.class);
				printIntent.setDataAndType(uri, data.getType());
				startActivityForResult(printIntent, REQUEST_PRINT);
			}
			else
			{
				Toast.makeText(this, "Cannot read file.",
						Toast.LENGTH_LONG).show();
			}
		} else if (requestCode == REQUEST_ENIAC) {
			// TODO ENIAC Printing not implemented
			Toast.makeText(this, "ENIAC Printing not implemented.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			//SD CARD
			Intent intent = new Intent(this, LocalFileBrowser.class);
			startActivityForResult(intent, REQUEST_SDCARD);
		} else if (position == 1) {
			//Remote ENIAC Server
			LoginScreen.resetConnection();
			Intent intent = new Intent(this, RemoteFileBrowser.class);
			startActivityForResult(intent, REQUEST_ENIAC);
		} else if (position == 2) {
			//FilePicker API
			Intent intent = new Intent(this, FilePicker.class);
			startActivityForResult(intent, FilePickerAPI.REQUEST_CODE_GETFILE);
		}
		else
		{
			//3rd party file browser
			int item_index=position-3;
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			intent.setPackage(m3rdPartyPackageName.get(item_index));
			startActivityForResult(intent, REQUEST_THIRDPARTY);
		}
			
	}

}