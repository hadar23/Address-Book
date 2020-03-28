import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public class ListIteratorButton extends CommandButton {
	private LinkedHashMap<Entry, Entry> map = new LinkedHashMap<>();
	private ListIterator<Entry> lit;
	private boolean flagFirstClick = false;

	public ListIteratorButton(AddressBookPane pane, RandomAccessFile r) {
		super(pane, r);
		this.setText("Iter");
		try {
			lit = listIterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Execute() {
		Iterator<Entry> it;
		if (!flagFirstClick) {
			flagFirstClick = true;
			while (lit.hasNext()) {
				Entry ent = lit.next();
				map.put(ent, ent);
			}
			it = map.values().iterator();
		} else {
			TreeSet<Entry> tree = new TreeSet<>(new Comparator<Entry>() {
				@Override
				public int compare(Entry o1, Entry o2) {
					int x = o1.getStreet().compareTo(o2.getStreet());
					return x == 0 ? 1 : x;
				}
			});
			tree.addAll(map.values());
			it = tree.iterator();
		}
		try {
			updateFile(lit, it);
			readAddress(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void updateFile(ListIterator<Entry> lit, Iterator<Entry> it) throws IOException {
		while (lit.hasPrevious()) {
			lit.previous();
			lit.remove();
		}
		raf.setLength(0);
		while (it.hasNext())
			lit.add(it.next());
	}

	/** Retrieves a ListIterator */
	public ListIterator<Entry> listIterator(int index) throws FileNotFoundException, IOException {
		return new ListIter(index);
	}

	public ListIterator<Entry> listIterator() throws FileNotFoundException, IOException {
		return listIterator(0);
	}

	private class ListIter implements ListIterator<Entry> {
		private int current = 0;
		private int last = -1;
		private long numRecords;

		public ListIter(int index) throws IOException {
			this.current = index;
			this.numRecords = raf.length() / (2 * RECORD_SIZE);
		}

		@Override
		public boolean hasNext() {
			return current < numRecords;
		}

		@Override
		public Entry next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Entry ent = null;
			try {
				ent = readEntry(current * (2 * RECORD_SIZE));
			} catch (IOException e) {
				e.printStackTrace();
			}
			last = current;
			current++;
			return ent;
		}

		@Override
		public boolean hasPrevious() {
			return current > 0;
		}

		@Override
		public Entry previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			Entry ent = null;
			current--;
			try {
				ent = readEntry(current * (2 * RECORD_SIZE));
			} catch (IOException e) {
				e.printStackTrace();
			}
			last = current;
			return ent;
		}

		@Override
		public int nextIndex() {
			return current;
		}

		@Override
		public int previousIndex() {
			return current - 1;
		}

		@Override
		public void remove() {
			if (last == -1)
				throw new IllegalStateException();
			try {
				ArrayList<Entry> lst = fileToArrayList();
				lst.remove(last);
				arrayListToFile(lst);
				numRecords--;
				current = last;
				last = -1;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void set(Entry ent) {
			if (last == -1)
				throw new IllegalStateException();
			try {
				raf.seek(last * (2 * RECORD_SIZE));
				writeEntry(last * (2 * RECORD_SIZE), ent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void add(Entry ent) {
			if (current < 0 || current > 2 * RECORD_SIZE)
				throw new IndexOutOfBoundsException();
			ArrayList<Entry> lst;
			try {
				lst = fileToArrayList();
				lst.add(current, ent);
				arrayListToFile(lst);
				numRecords++;
				current++;
				last = -1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private ArrayList<Entry> fileToArrayList() throws IOException {
			ArrayList<Entry> lst = new ArrayList<>();
			raf.seek(0);
			while (raf.getFilePointer() < raf.length()) {
				lst.add(readEntry(raf.getFilePointer()));
			}
			return lst;
		}

		private void arrayListToFile(ArrayList<Entry> lst) throws IOException {
			raf.seek(0);
			raf.setLength(0);
			for (Entry ent : lst)
				writeEntry(raf.getFilePointer(), ent);
		}

	}
}