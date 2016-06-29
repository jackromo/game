package net.parseltounge.components.ai_comp;

import net.parseltounge.entities.Entity;
import net.parseltounge.entities.living_ents.EnemyEntity;
import net.parseltounge.entities.living_ents.PlayerEntity;

import java.util.ArrayList;

public class EnemyAIUpdater {

    private EnemyAI chosenAI;

    public EnemyAIUpdater(EnemyAI chosen_ai) {
        set_chosen_ai(chosen_ai);
    }

    public void set_chosen_ai(EnemyAI chosen_ai) {
        chosenAI = chosen_ai;
    }

    public void update_enemy(EnemyEntity enemy, ArrayList<Entity> ents) {
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
                update_chase_still(enemy, ents);
                break;
            default:
                break;
        }
    }

    private void update_still(EnemyEntity enemy) {
        enemy.set_dx(0);
    }

    private void update_walk_no_turn(EnemyEntity enemy) {
        if(enemy.facing_left) {
            enemy.set_dx(-5);
        } else {
            enemy.set_dx(5);
        }
    }

    private void update_walk_turn(EnemyEntity enemy) {
        if(enemy.facing_wall)
            enemy.facing_left = !enemy.facing_left;
        if(enemy.facing_left) {
            enemy.set_dx(-5);
        } else {
            enemy.set_dx(5);
        }
    }

    private void update_chase_still(EnemyEntity enemy, ArrayList<Entity> ents) {
        enemy.set_dx(0);    // if not changed, will be reset to 0
        for(Entity e: ents) {
            if(e instanceof PlayerEntity) {
                double dist_squared = Math.pow(e.get_x() - enemy.get_x(), 2) +
                        Math.pow(e.get_y() - enemy.get_y(), 2);
                // preset chasing distance is 1000
                if(dist_squared < Math.pow(1000, 2)) {
                    if(enemy.get_x() < e.get_x()) {
                        enemy.set_dx(5);
                    } else {
                        enemy.set_dx(-5);
                    }
                }
                break;
            }
        }
    }

}
