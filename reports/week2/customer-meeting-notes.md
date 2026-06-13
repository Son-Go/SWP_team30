> This report contains transcript and notes files at the same time because customer meeting was about 1 hour long and model, which transformed recording into text was not too precise 

### 1. Defining MVP boundaries and priorities

The team presented to the customer a gradation of features according to the methodology (such as MoSCoW / Must-Could) and agreed on the stages of development:

- **As part of the first backend (the most basic level):** The main focus is on the architecture of the database, the implementation of **game cards** (as the main section of the platform) and the minimal architecture of the developer profile.
    
- **Authorization and regular users:** A full-fledged authorization system and specific functionality for ordinary users (likes, profiles) will not be included in the very first version. The customer confirmed that at this stage the focus is solely on developer cards and profiles.
    
- **Future plans (after MVP):** In the following iterations will be added:
    
    1. Events section (parsing, display, archive and the opportunity to propose an event).
        
    2. The merch store.
        
    3. The forum (the customer agreed to make it last or completely removed from the priority plans, since forums, for example, the Innopolis forum, are often inactive).
        

### 2. Discussion of the interface and game cards

The customer generally approved the current design concept and color scheme, noting that they look modern and can be easily customized.

- **Mini profiles (hover previews):** The customer really liked the idea of a mini-profile of the game, which opens when you hover over the card (similar to Steam). Screenshots and a brief description of the project should be displayed there. The team also mentioned that they had considered the cards in Roblox as a reference.
    
- **Card Content:** Will include the name, image of the game, description and tags at the bottom. The customer approved the addition of a "category", which allocates the card with two slots at once for updates and updates.
    

### 3. The functionality of ratings, tags and filtering

- **Assessment system:** To simplify the logic, we decided to abandon complex scales or asterisks. The ratings will be in the **"I recommend" format. / I do not recommend it"** (as in Steam). The customer agreed that with such a dual system, users are more likely to leave positive feedback.
    
- **Gamification and rating:** The team suggested that in the future developers should be awarded points for activity on the site, which can be spent on buying merchandise (T-shirts, mats, pens). The customer noted that this is a good loophole for returning to the topic of gamification, which he is interested in.
    
- **Tags:** There must be a fixed set of tags, administered strictly from above (through the admin panel), so that users do not produce identical entities with typos or in different languages (for example, "horror" and "horror").
    
- **Sorting and cheating:** The customer opposed the use of "views" as a metric of popularity or quality of the game, arguing that views are easily scrolled with the F5 key and do not reflect real interest. Instead, it is better to build a ranking based on comments, recommendations, or through a manual tool — **Editors' Choice** (a separate tab with a rotating weekly pool of games).
    

### 4. Server infrastructure

- The team reported that they have allocated one server from Innopolis for development.
    
- Due to possible limitations or inconveniences of the Innopolis server, the parties came to the conclusion that it is better to rent/buy a separate independent server and independently link the domain to it. The customer and the team confirmed their willingness to do this on their own.