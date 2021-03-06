package com.engineering.printer;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SeparatedListAdapter extends BaseAdapter {
	
	public final Map<String,BaseAdapter> sections = new LinkedHashMap<String,BaseAdapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;
	
	public SeparatedListAdapter(Context context) {
		headers = new ArrayAdapter<String>(context, R.layout.section_item);
	}
	
	private boolean useConvertView = true;
	public void setUseConvertView(boolean useConvertView)
	{
		this.useConvertView = useConvertView;
	}
	
	/**
	 * Get the lowest item position of a section.
	 * @param sectionIndex the index of the section
	 * @return the index of the first item in this section. Index starts from zero.
	 * @throws IllegalArgumentException if the section index is out of range
	 */
	public int getLowestIndex(int sectionIndex) throws IllegalArgumentException
	{
		if(sectionIndex < 0 || sectionIndex >= sections.size())
		{
			throw new IllegalArgumentException();
		}
		int lowestIndex = 0;
		for(int i=0; i < headers.getCount()&&i<sectionIndex; i++)
		{
			lowestIndex += sections.get(headers.getItem(i)).getCount();			
		}
		//Also count the section headers.
		lowestIndex += sectionIndex + 1;
		return lowestIndex;
		
	}
	
	public void addSection(String section, BaseAdapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);
	}
	
	public Object getItem(int position) {
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			// check if position inside this section 
			if(position == 0) return section;
			if(position < size) return adapter.getItem(position - 1);

			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for(Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for(Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}
	
	public int getItemViewType(int position) {
		int type = 1;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			// check if position inside this section 
			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}
	
	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		//Disable convertView when required,
		//Work around http://code.google.com/p/android/issues/detail?id=17128
		if(!useConvertView)
			convertView = null;
		
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			// check if position inside this section
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		//Disable convertView when required,
		//Work around http://code.google.com/p/android/issues/detail?id=17128
		if(!useConvertView)
			convertView = null;
		int sectionnum = 0;
		for(Object section : this.sections.keySet()) {
			BaseAdapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			
			// check if position inside this section
			if(position == 0) return headers.getDropDownView(sectionnum, convertView, parent);
			if(position < size) return adapter.getDropDownView(position - 1, convertView, parent);

			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	

}
