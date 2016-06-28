package net.parseltounge.components.ai_comp;

import net.parseltounge.entities.living_ents.EnemyEntity;

/**
 * Created by Jack on 6/28/2016.
 */
public class EnemyAIUpdater {

    private EnemyAI chosenAI;

    void update_enemy(EnemyEntity enemy) {
        switch(chosenAI) {
            case STILL:
                update_chase_still(enemy);
                break;
            case WALK_NO_TURN:
                update_walk_no_turn(enemy);
                break;
            case WALK_TURN:
                update_walk_turn(enemy);
                break;
            case CHASE_STILL:
                update_chase_still(enemy);
                break;
            default:
                break;
        }
    }

    void update_still(EnemyEntity enemy) {
        //
    }

    void update_walk_no_turn(EnemyEntity enemy) {
        //
    }

    void update_walk_turn(EnemyEntity enemy) {
        //
    }

    void update_chase_still(EnemyEntity enemy) {
        //
    }

}
