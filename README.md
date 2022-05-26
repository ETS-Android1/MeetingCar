# MeetingCar
Projet de Master 1 - Programmation Mobile

## Vidéo de présentation

https://youtu.be/x_SkfI725RY

## Mise en place de l'application

Pour cette partie il est nécessaire d'avoir Android Studio installé. 
Vous pourrez trouver dans la base du code, au sein du dossier MeetingCarAndroid le projet Android.
        
Vous allez devoir l'ouvrir avec Android Studio pour pouvoir le compiler. Si vous souhaitez utiliser un serveur autre que notre serveur en ligne vous pouvez modifier BASE_URL dans la classe fr.flareden.meetingcar.Config pour mettre la valeur de votre serveur.
        
Pour créer votre serveur veuillez suivre les sections suivantes.

## Mise en place de la base de données

Pour cette partie il est nécessaire d'avoir un serveur MySQL installé et configuré.
Vous pourrez trouver dans la base de code, au sein du dossier MeetingCarDatabase, le fichier meeting_car_database.sql permettant la génération avec les références de la base de données meeting_car.

Vous pouvez l'importer dans PhpMyAdmin ou l'exécuter dans le terminal SQL.

## Mise en place du serveur

Pour cette partie il est nécessaire d'avoir Node.JS installé. Vous pourrez trouver dans la base de code, au sein du dossier MeetingCarWebservice le serveur web du projet.
        
Pour le faire fonctionner vous devez d'abord avoir mis en place la base de données, puis il faudra créer un fichier access.json. Ce fichier est une copie de empty_access.json, sur lequel il faut remplir les différents détails.
        
Pour l'exécuter vous pouvez simplement faire la commande "node webservice.js".
