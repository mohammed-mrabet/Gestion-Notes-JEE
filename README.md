# 📚 MiniProjet_JEE - Gestion des Notes (BE2 + MVC2)

[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue.svg)](https://jakarta.ee/)
[![JSF](https://img.shields.io/badge/JSF-4.0-green.svg)](https://jakarta.ee/specifications/faces/)
[![JPA](https://img.shields.io/badge/JPA-3.1-orange.svg)](https://jakarta.ee/specifications/persistence/)
[![WildFly](https://img.shields.io/badge/WildFly-38-red.svg)](https://www.wildfly.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![NetBeans](https://img.shields.io/badge/NetBeans-19+-yellow.svg)](https://netbeans.apache.org/)

---

## 📖 Description

Application web complète de **gestion des notes** développée dans le cadre du module **Jakarta EE**. 
Elle permet aux administrateurs de gérer les utilisateurs, aux professeurs de saisir les notes 
des étudiants, et aux étudiants de consulter leurs résultats et générer leurs bulletins.

### 🎯 Objectifs pédagogiques

- ✅ Maîtriser l'architecture **MVC2 avec JSF**
- ✅ Implémenter le pattern **DAO** (Data Access Object)
- ✅ Utiliser **JPA** pour la persistance des données
- ✅ Gérer l'**authentification** et les **rôles** (Admin/Professeur/Étudiant)
- ✅ Générer des rapports **PDF** et **CSV**
- ✅ Respecter les bonnes pratiques de sécurité (BCrypt, XSS, injection SQL)

---

## 👥 Rôles et fonctionnalités

### 👨‍💼 Administrateur
- ✅ CRUD complet des utilisateurs
- ✅ Gestion des modules et matières
- ✅ Gestion des affectations (professeur ↔ matière)
- ✅ Statistiques globales

### 👨‍🏫 Professeur
- ✅ Saisie des notes par matière
- ✅ Consultation de la liste des étudiants
- ✅ Calcul automatique des moyennes
- ✅ Statistiques par matière
- ✅ Export des notes en CSV

### 👨‍🎓 Étudiant
- ✅ Consultation de ses notes
- ✅ Calcul automatique des moyennes
- ✅ Visualisation des appréciations
- ✅ Téléchargement du bulletin en PDF
- ✅ Export des notes en CSV

---

## 🛠️ Stack technique

| Technologie | Version | Description |
|-------------|---------|-------------|
| **Jakarta EE** | 10 | Plateforme Java EE standard |
| **JSF (MVC2)** | 4.0 | Interface utilisateur |
| **JPA** | 3.1 | Persistance des données |
| **EclipseLink** | 4.0.5 | Provider JPA |
| **MySQL** | 8.0 | Base de données relationnelle |
| **WildFly** | 38 | Serveur d'application |
| **NetBeans** | 19+ | IDE de développement (Ant) |
| **BCrypt** | 0.4 | Hachage des mots de passe |
| **iTextPDF** | 5.5.13.2 | Génération de rapports PDF |
| **MVC2** | - | Architecture JSF |

---

## 📂 Structure du projet
MiniProjet_JEE/
├── src/
│ └── java/
│ ├── beans/ # Entités JPA (9 classes)
│ │ ├── Utilisateur.java
│ │ ├── Administrateur.java
│ │ ├── Professeur.java
│ │ ├── Etudiant.java
│ │ ├── Module.java
│ │ ├── Matiere.java
│ │ ├── Note.java
│ │ ├── Inscription.java
│ │ └── GroupeTP.java
│ │
│ ├── controllers/ # JSF Beans (7 classes)
│ │ ├── AuthentificationController.java
│ │ ├── UtilisateurController.java
│ │ ├── ProfesseurController.java
│ │ ├── ModuleController.java
│ │ ├── NoteController.java
│ │ ├── AffectationController.java
│ │ └── BulletinController.java
│ │
│ ├── dao/ # Pattern DAO
│ │ ├── interfaces/ # 5 interfaces
│ │ │ ├── IUtilisateurDAO.java
│ │ │ ├── IProfesseurDAO.java
│ │ │ ├── IModuleDAO.java
│ │ │ ├── INoteDAO.java
│ │ │ └── IInscriptionDAO.java
│ │ └── implementations/ # 5 implémentations
│ │ ├── UtilisateurDAO.java
│ │ ├── ProfesseurDAO.java
│ │ ├── ModuleDAO.java
│ │ ├── NoteDAO.java
│ │ └── InscriptionDAO.java
│ │
│ ├── services/ # Logique métier (8 classes)
│ │ ├── AuthentificationService.java
│ │ ├── UtilisateurService.java
│ │ ├── ProfesseurService.java
│ │ ├── ModuleService.java
│ │ ├── NoteService.java
│ │ ├── ExportService.java
│ │ ├── GroupeTPService.java
│ │ └── AffectationService.java
│ │
│ ├── exceptions/ # Exceptions personnalisées
│ │ ├── DAOException.java
│ │ ├── ServiceException.java
│ │ └── ValidationException.java
│ │
│ └── utils/ # Utilitaires
│ ├── JpaUtil.java
│ ├── PasswordUtils.java
│ ├── DateUtils.java
│ └── LocalDateConverter.java
│
├── web/
│ ├── views/ # Pages XHTML (16 pages)
│ │ ├── login.xhtml
│ │ ├── accueil.xhtml
│ │ ├── error.xhtml
│ │ ├── admin/
│ │ │ ├── dashboard.xhtml
│ │ │ ├── gestionUtilisateurs.xhtml
│ │ │ ├── gestionModules.xhtml
│ │ │ ├── gestionAffectations.xhtml
│ │ │ └── gestionNotes.xhtml
│ │ ├── professeur/
│ │ │ ├── dashboard.xhtml
│ │ │ ├── saisieNotes.xhtml
│ │ │ └── listeEtudiants.xhtml
│ │ └── etudiant/
│ │ ├── dashboard.xhtml
│ │ ├── mesNotes.xhtml
│ │ └── bulletin.xhtml
│ │
│ ├── includes/ # Templates
│ │ ├── header.xhtml
│ │ └── footer.xhtml
│ │
│ ├── css/
│ │ └── style.css
│ │
│ └── WEB-INF/
│ ├── web.xml
│ ├── faces-config.xml
│ ├── beans.xml
│ └── jboss-web.xml
│
├── lib/ # Dépendances JAR
│ ├── eclipselink-4.0.5.jar
│ ├── mysql-connector-j-8.0.33.jar
│ ├── jbcrypt-0.4.jar
│ └── itextpdf-5.5.13.2.jar
│
├── .gitignore
├── build.xml # Configuration Ant
└── README.md

---

## 🔐 Comptes de test

| Rôle | Login | Mot de passe | Description |
|------|-------|--------------|-------------|
| 👨‍💼 **Administrateur** | `admin` | `admin123` | Gestion complète du système |
| 👨‍🏫 **Professeur** | `k.alami` | `prof123` | Saisie des notes (Informatique) |
| 👨‍🏫 **Professeur** | `s.benani` | `prof123` | Saisie des notes (Mathématiques) |
| 👨‍🎓 **Étudiant** | `y.cherkaoui` | `etudiant123` | Consultation des notes |
| 👨‍🎓 **Étudiant** | `f.daoudi` | `etudiant123` | Consultation des notes |
| 👨‍🎓 **Étudiant** | `a.elmansouri` | `etudiant123` | Consultation des notes |

---

## 🚀 Installation et déploiement

### Prérequis

| Logiciel | Version | Lien |
|----------|---------|------|
| **JDK** | 21+ | [Oracle](https://www.oracle.com/java/technologies/downloads/) |
| **WildFly** | 38 | [WildFly](https://www.wildfly.org/downloads/) |
| **MySQL** | 8.0 | [MySQL](https://dev.mysql.com/downloads/mysql/) |
| **NetBeans** | 19+ | [NetBeans](https://netbeans.apache.org/) |

### Étape 1 : Cloner le projet

```bash
git clone https://github.com/mohammed-mrabet/Gestion-Notes-JEE.git

Étape 2 : Créer la base de données

Connectez-vous à MySQL et exécutez le script :
-- Créer la base de données
CREATE DATABASE bd_m2i_s1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer l'utilisateur dédié
CREATE USER 'admin_m2i'@'localhost' IDENTIFIED BY 'master2025';
GRANT ALL PRIVILEGES ON bd_m2i_s1.* TO 'admin_m2i'@'localhost';
FLUSH PRIVILEGES;

-- Utiliser la base
USE bd_m2i_s1;

-- Créer les tables (utiliser le script complet fourni dans le projet)
-- ...

Étape 3 : Configurer WildFly

  1-Démarrer WildFly
    cd C:\wildfly-38.0.1.Final\bin
    standalone.bat
  2-Créer un utilisateur admin
    add-user.bat
    # Choisir 'a' pour Management User
    # Login: admin
    # Mot de passe: admin123
  3- Ajouter le driver MySQL (si non présent)

Étape 4 : Importer le projet dans NetBeans

    File → Open Project

    Naviguer vers le dossier du projet

    Sélectionner le projet

    Cliquer sur "Open Project"

Étape 5 : Configurer le projet dans NetBeans

    Clic droit sur le projet → Properties

    Frameworks → JPA (vérifier la configuration)

    Libraries (vérifier les JARs)

    Run → Server : WildFly

Étape 6 : Déployer l'application

    Clic droit sur le projet → Clean and Build

    Clic droit sur le projet → Run

Étape 7 : Tester l'application

Ouvrez votre navigateur et allez sur :
  http://localhost:8080/MiniProjet_JEE/views/login.xhtml

  Connectez-vous avec les identifiants de test.

┌─────────────────────────────────────────────────────────────┐
│                         UTILISATEUR                         │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                         VUE (JSF)                          │
│                    Pages XHTML + CSS                        │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLEUR (JSF Beans)                   │
│              @Named, @SessionScoped                         │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE (Métier)                       │
│               Logique métier et transactions                │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                        DAO (Données)                        │
│              Accès à la base de données via JPA             │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                       BASE DE DONNÉES                       │
│                          MySQL                              │
└─────────────────────────────────────────────────────────────┘

Diagramme de classes (simplifié)

                    ┌─────────────────┐
                    │   Utilisateur   │
                    ├─────────────────┤
                    │ - id: int       │
                    │ - nom: String   │
                    │ - prenom: String│
                    │ - login: String │
                    │ - motDePasse:   │
                    │   String        │
                    │ - role: Role    │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │ Administrateur  │ │   Professeur    │ │    Etudiant     │
    ├─────────────────┤ ├─────────────────┤ ├─────────────────┤
    │ - fonction:     │ │ - specialite:   │ │ - cne: String   │
    │   String        │ │   String        │ │ - dateNaissance:│
    └─────────────────┘ │ - grade: String │ │   LocalDate     │
                        └────────┬────────┘ │ - adresse:      │
                                 │          │   String        │
                                 │          │ - classe: String│
                                 │          └─────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
          ┌─────────────────┐       ┌─────────────────┐
          │     Matiere     │       │      Note       │
          ├─────────────────┤       ├─────────────────┤
          │ - code: String  │◄──────│ - note: double  │
          │ - nom: String   │       │ - session:      │
          │ - coefficient:  │       │   String        │
          │   int           │       └─────────────────┘
          └────────┬────────┘
                   │
                   │
                   ▼
          ┌─────────────────┐
          │     Module      │
          ├─────────────────┤
          │ - code: String  │
          │ - nom: String   │
          │ - coefficient:  │
          │   int           │
          └─────────────────┘

👨‍💻 Auteurs
  MRABET MOHAMMED

📅 Date
  Février 2026