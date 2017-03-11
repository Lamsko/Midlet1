import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class Midlet1 extends MIDlet implements CommandListener {
	
	private static Display wyswietlacz;
	TextBox tb;
	TextBox okno2;
	private Command o2;
	private Command koniec;
	static RecordStore magazyn;

	public Midlet1() {
		// TODO Auto-generated constructor stub
		wyswietlacz = Display.getDisplay(this);
		tb = new TextBox("Moj Midlet1", "Witaj uzytkowniku!", 256,0);
		koniec = new Command("Koniec",Command.EXIT,1);
		tb.addCommand(koniec);
		o2 = new Command("Okno 2", Command.SCREEN,1);
		tb.addCommand(o2);
		okno2 = new Ekran2(tb);
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		System.err.println("*** Wywolano destroyApp ***");
		try {
			magazyn.closeRecordStore();
		} 
		catch (RecordStoreException ex) {
			ex.printStackTrace();
		}
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		wyswietlacz.setCurrent(tb);
		tb.setCommandListener((CommandListener)this);
		try {
			magazyn = RecordStore.openRecordStore("Wpisy", true, 
					RecordStore.AUTHMODE_PRIVATE, false);
		}
		catch (RecordStoreException ex) {
			ex.printStackTrace();
		}
	}

	public void commandAction(Command komenda, Displayable elemEkranu) {
		// TODO Auto-generated method stub
		if(komenda == koniec) {
			try {
				destroyApp(false);
			} catch (MIDletStateChangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			notifyDestroyed();
		}
		else if (komenda == o2) {
			wyswietlacz.setCurrent(okno2);
		}
	}
	
	public class Ekran2 extends TextBox implements CommandListener {
		
		private Command powrot;
		private Displayable ekranP;
		private Command zapisz;
		private Command wyswietl;
		private RecordEnumeration iterator;

		public Ekran2(Displayable ekranPowrotny) {
			super("Okno 2", "Witaj w oknie 2", 256, 0);
			wyswietlacz = Midlet1.mojDisplay();
			ekranP = ekranPowrotny;
			powrot = new Command("Powrot", Command.BACK,1);
			zapisz = new Command("Zapisz", Command.ITEM,2);
			wyswietl = new Command("Wyswietl", Command.ITEM,1);
			this.addCommand(wyswietl);
			this.addCommand(zapisz);
			this.addCommand(powrot);
			this.setCommandListener(this);
		}

		public void commandAction(Command komenda, Displayable d) {
			// TODO Auto-generated method stub
			if (komenda == powrot) {
				wyswietlacz.setCurrent(ekranP);
			}
			if (komenda == zapisz) {
				byte[] rekord = this.getString().getBytes();
				if(rekord.length > 0)
					try {
						Midlet1.magazyn.addRecord(rekord, 0, rekord.length);
						this.setString("...zapisane");
					}
					catch (RecordStoreException ex){
						ex.printStackTrace();
					}
				else this.setString("...nic nie zapisano");
			}
			if (komenda == wyswietl) {
				String calyTekst = "";
				try {
					iterator = Midlet1.magazyn.enumerateRecords(null, 
							new KomparatorTekstu(), false);
					while (iterator.hasNextElement()) {
						byte[] rekord = iterator.nextRecord();
						String tekst = new String(rekord);
						calyTekst += (tekst + "\n");
					}
				}
				catch (RecordStoreException ex) {
					ex.printStackTrace();
				}
				this.setString(calyTekst);
			}
		}

	}
	
	public class KomparatorTekstu implements RecordComparator {

		public int compare(byte[] rec1, byte[] rec2) {
			// TODO Auto-generated method stub
			String arg1 = new String(rec1);
			String arg2 = new String(rec2);
			
			int relacja = arg1.compareTo(arg2);
			if (relacja < 0)
				return -1;
			else if (relacja > 0)
				return 1;
			else 
				return 0;
		}
		
	}

	public static Display mojDisplay() {
		// TODO Auto-generated method stub
		return wyswietlacz;
	}

}
