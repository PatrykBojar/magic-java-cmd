package main;

/**
 * A simple pretty console utilities, like messages or prints. Pure
 * beautification purposes. <br>
 * Cannot be instantiated.
 * 
 * @author Patryk Bojar
 * @version 1.0.1 02 Dec 2021
 */
public final class ConsolePretty {
	/**
	 * Main private Constructor.
	 */
	private ConsolePretty() {

	}

	/**
	 * Writes a welcome message with the system information and time.
	 * 
	 * @throws InterruptedException a exception if thread is interrupted.
	 */
	public static void showStartMessage() throws InterruptedException {
		System.out.println("I.A.C.C [Versión 92.1.9.1939]");
		System.out.println("(c) Cerberus Corp. Todos los derechos reservados.");

		System.out.println("\nInicializando el sistema...");

		System.out.print("\n[0%...");
		Thread.sleep(100);
		System.out.print("14%...");
		Thread.sleep(100);
		System.out.print("37%...");
		Thread.sleep(100);
		System.out.print("76%...");
		Thread.sleep(100);
		System.out.print("99%...");
		Thread.sleep(100);
		System.out.print("100%]\n");

		Thread.sleep(300);
		System.out.println("\nCarga completa. Sistema inicializado.");
		System.out.println("Bienvenido a I.A.C.C.");

		Console.getCurrentTime();

		System.out.println("");
	}

	/**
	 * Writes a goodbye message.
	 * 
	 * @throws InterruptedException a exception if thread is interrupted.
	 */
	public static void showGoodByeMessage() throws InterruptedException {
		System.out.println("\nCerrando todos los exec-79...");
		System.out.println("Reduciendo el flujo de energía cuántica...");

		System.out.print("[++-+-+");
		Thread.sleep(50);
		System.out.print("+-+-+");
		Thread.sleep(50);
		System.out.print("+-+-+");
		Thread.sleep(50);
		System.out.print("+-+-++]");
		Thread.sleep(300);

		System.out.println("\n\nApagando los sistemas subcore-12...");

		System.out.println("Neutralizando el núcleo...");
		System.out.print("[0%...");
		Thread.sleep(50);
		System.out.print("58%...");
		Thread.sleep(50);
		System.out.print("91%...");
		Thread.sleep(50);
		System.out.print("100%...]");
		System.out.println("\nNúcleo neutralizado.");

		System.out.println("\nSistema apagdo.");

	}

	/**
	 * Shows all console commands and writes their information.
	 */
	public static void showHelpMessage() {
		System.out.println("");
		System.out.println("([parámetro]) Indica que el parámetro es opcional.\n");
		System.out.println(
				"mueve         [ruta/archivo] [ruta]            Mueve un archivo de un directorio a otro, eliminándolo del origen.");
		System.out.println("copia         [ruta/archivo] [ruta]            Copia un archivo a otra ubicación."
				+ " en la misma ruta.");
		System.out.println("elimina       [ruta/archivo] -                 Elimina un archivo.");
		System.out.println(
				"lista         ([ruta])       -                 Muestra una lista de archivos y subdirectorios en un directorio.");
		System.out.println(
				"listaArbol    ([ruta])       -                 Muestra de forma gráfica la estructura de carpetas de una unidad o ruta.");
		System.out.println(
				"muestraTXT    [ruta/archivo] -                 Lee y muestra el contenido de un archivo de texto con la extensión .txt.");
		System.out.println(
				"muestraXML    [ruta/archivo] [/modificador]    Lee y muestra el contenido de un archivo XML con la extensión .xml.");
		System.out.println(
				"comparaTXT    [ruta/archivo] [ruta/archivo]    Compara y muestra si dos ficheros de texto son iguales línea a línea.");
		System.out.println(
				"ayuda         -              -                 Proporciona información de ayuda para los comandos"
						+ " de I.A.C.C.");
		System.out.println("salir         -              -                 Termia la ejecución y cierra la consola.");
		System.out.println("");
	}
}
