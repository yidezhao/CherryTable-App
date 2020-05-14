import React from 'react';
import {Route, Redirect} from 'react-router-dom';
import {connect} from 'react-redux';
const UserRoutes=({isAuth,componenet:Component,...Rest})=>{

    return (
    <Route {...Rest} render=
    {props=>
        isAuth?<Component{...props}/>:<Redirect to="/"/>}/>
)
    }
function MapStatetoProps(state){
    
    return {
        isAuth: !!state.user.success
    };
}
export default connect(MapStatetoProps)(UserRoutes);