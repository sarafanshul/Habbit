package com.projectdelta.habbit.ui.activity.editTask.state

sealed class EditTaskInteractionState{

    companion object{
        const val STATE_EDIT  = "StateEdit"
        const val STATE_DEFAULT = "StateDefault"
    }

    class EditState : EditTaskInteractionState(){

        override fun toString(): String {
            return STATE_EDIT
        }

    }

    class DefaultState : EditTaskInteractionState() {

        override fun toString(): String {
            return STATE_DEFAULT
        }
    }

}
