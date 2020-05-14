import {USER_LOGGED_IN} from './types';
const initailState = {
    email:'',
    token:''
    }
const user=(state=initailState,action={})=>{
    if (action.type=="USER_LOGGED_IN"){
        return action.user;
    } 
        else{
             return state;
        }
    
}
export default user;