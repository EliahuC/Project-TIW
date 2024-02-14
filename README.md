# Project-TIW
Progetto realizzato per il corso di Tecnologie Informatiche Del Web del Politecnico di Milano nell'anno 2023

Voto conseguito : 29

## CREDITS  
[Eliahu Itamar Cohen](https://github.com/EliahuC)    
[Lorenzo Fonnesu](https://github.com/Fonzy-01)
 
# TRACCIA : Catalogazione di immagini 

## Versione HTML pura:  

Un’applicazione permette all’utente (ad esempio il responsabile dei servizi ambientali di una regione) di gestire una collezione di immagini satellitari e una tassonomia di classificazione utile per etichettare immagini allo scopo di consentire la ricerca per categoria. Dopo il login, l’utente accede a una pagina HOME in cui compare un albero gerarchico di categorie. Le categorie non dipendono dall’utente e sono in comune tra tutti gli utenti. Un esempio di un ramo dell’albero è il seguente:  

9 Materiali solidi>>copia  
91 Materiali inerti>>copia  
911 Inerti da edilizia >>copia  
9111 Amianto >>copia  
91111 Amianto in lastre >>copia  
91112 Amianto in frammenti >>copia  
9112 Materiali cementizi >>copia  
912 Inerti ceramici >>copia  
9121 Piastrelle >>copia  
9122 Sanitari >>copia  
92 Materiali ferrosi >>copia    

L’utente può inserire una nuova categoria nell’albero. Per fare ciò usa una form nella pagina HOME in cui specifica il nome della nuova categoria e sceglie la categoria padre. L’invio della nuova categoria comporta l’aggiornamento dell’albero: la nuova categoria è appesa alla categoria padre come ultimo sottoelemento. Alla nuova categoria viene assegnato un codice numerico che ne riflette la posizione (ad esempio, la nuova categoria “Amianto in tubi”, figlia della categoria “9111 Amianto” assume il codice 91113). Dopo la creazione di una categoria, la pagina HOME mostra l’albero aggiornato. Per velocizzare la costruzione della tassonomia l’utente può copiare un intero sottoalbero in una data posizione: per fare ciò clicca sul link “copia” associato alla categoria radice del sottoalbero da copiare. A seguito di tale azione l’applicazione mostra, sempre nella HOME page, l’albero con evidenziato il sottoalbero da copiare: tutte le altre categorie hanno un link “copia qui”. Ad esempio, a seguito del click sul link “copia” associato alla categoria “9111 Amianto” 
l’applicazione visualizza l’albero come segue.                            

9 Materiali solidi>>copia qui  
91 Materiali inerti>>copia qui  
911 Inerti da edilizia >>copia qui  
9111 Amianto  
91111 Amianto in lastre  
91112 Amianto in frammenti  
9112 Materiali cementizi >>copia qui  
912 Inerti ceramici >>copia qui  
9121 Piastrelle >>copia qui  
9122 Sanitari >>copia qui  
92 Materiali ferrosi >>copia qui  

La selezione di un link “copia qui” comporta l’inserimento di una copia del sottoalbero come ultimo figlio della categoria destinazione. Ad esempio, la selezione del link “copia qui” della categoria “9 Materiali solidi” comporta la seguente modifica dell’albero:  

9 Materiali solidi>>copia  
91 Materiali inerti>>copia  
911 Inerti da edilizia >>copia  
9111 Amianto  
91111 Amianto in lastre  
91112 Amianto in frammenti  
9112 Materiali cementizi >>copia  
912 Inerti ceramici >>copia  
9121 Piastrelle >>copia  
9122 Sanitari >>copia  
92 Materiali ferrosi >>copia  
93 Amianto >>copia  
931 Amianto in lastre >>copia  
932 Amianto in frammenti >>copia  

Le modifiche effettuate da un utente e salvate nella base di dati diventano visibili agli altri utenti.  
Per semplicità si ipotizzi che per ogni categoria il numero massimo di sottocategorie sia 9, numerate da 1 a 9. In questo caso l’operazione di copia deve controllare che lo spostamento non determini un numero di sottocategorie superiore a 9. Si preveda anche un link “copia qui” non associato a un nodo della tassonomia che permette di copiare un sotto-albero al primo livello della tassonomia (se non esistono già 9 nodi al primo livello della tassonomia)  

## Versione con JavaScript:  

Si realizzi un’applicazione client server web che estende e/o modifica le specifiche precedenti come segue:  
● Dopo il login dell’utente, l’intera applicazione è realizzata con un’unica pagina.  
● Ogni interazione dell’utente è gestita senza ricaricare completamente la pagina, ma produce l’invocazione asincrona del server e l’eventuale modifica del contenuto da aggiornare a seguito dell’evento.  
● La funzione di copia di un sottoalbero è realizzata mediante drag & drop. A seguito del drop della radice del sottoalbero da copiare compare una finestra di dialogo con cui l’utente può confermare o cancellare la copia. La conferma produce l’aggiornamento solo a lato client dell’albero. La cancellazione riconduce allo stato precedente al drag & drop. A seguito della conferma compare un bottone SALVA che consente il salvataggio a lato server della tassonomia modificata.  
● L’utente può cliccare sul nome di una categoria. A seguito di tale evento compare al posto del nome un campo di input contente la stringa del nome modificabile. L’evento di perdita del focus del campo di input produce il salvataggio nel database del nome modificato della categoria  
