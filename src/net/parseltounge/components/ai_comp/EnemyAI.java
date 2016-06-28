package net.parseltounge.components.ai_comp;

public enum EnemyAI {
    STILL,          // Stand still at all times
    WALK_NO_TURN,   // Walk in a direction without turning on collision
    WALK_TURN,      // Walk in a direction, turn at wall collisions
    CHASE_STILL     // Stand still, if player in a range will follow them
}
