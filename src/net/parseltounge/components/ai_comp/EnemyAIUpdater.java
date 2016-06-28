package net.parseltounge.components.ai_comp;

import net.parseltounge.entities.living_ents.EnemyEntity;

public class EnemyAIUpdater {

    private EnemyAI chosenAI;

    public EnemyAIUpdater(EnemyAI chosen_ai) {
        set_chosen_ai(chosen_ai);
    }

    public void set_chosen_ai(EnemyAI chosen_ai) {
        chosenAI = chosen_ai;
    }
    
    public void update_enemy(EnemyEntity enemy) {
        switch(chosenAI) {
            case STILL:
                update_still(enemy);
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

    private void update_still(EnemyEntity enemy) {
        //
    }

    private void update_walk_no_turn(EnemyEntity enemy) {
        //
    }

    private void update_walk_turn(EnemyEntity enemy) {
        //
    }

    private void update_chase_still(EnemyEntity enemy) {
        //
    }

}
