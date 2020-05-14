import React from "react";
import loginImg from "../style/1.jpg";
import {Redirect} from 'react-router-dom';
import AppHome from '../components/App.js';
import InlineError from './InlineError.js';
export class Login extends React.Component {
  state={
      redirect: false,
      data:{
        email:"",
        username:"",
        loading:false,
        password:"",
      },
      errors:{}
    };
  
  
  
  onChange= e=>{
    this.setState({
      data:{...this.state.data,[e.target.name]:[e.target.value]}
    });
  }
  onSubmit = (e)=>{
    const errors = this.validate(this.state.error);
    this.setState({errors});
    if (Object.keys(errors).length==0){
      
      this.props.submit(this.state.data).catch(err=>{
        if (err.response && err.response.data) {this.setState({errors:err.response.data.errors,loading:false});
      }});
    }
    e.preventDefault();

  }
  validate = ()=>{
    const errors={};
    
    if (!this.state.data.username){
      errors.username="Can't be blank.";
    }
    if (!this.state.data.password){
      errors.password="Can't be blank.";
    }
    return errors;
  }
  
  render() {
    return (
      <div className="base-container" ref={this.props.containerRef}>
        
        <form onSubmit={this.onSubmit}>
        <h1>Login</h1>
        <div className="content">
          
          <div className="form">
            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input type="text" name="username" placeholder="username" value={this.state.data.username} onChange={this.onChange}/>
              {this.state.errors && <InlineError text={this.state.errors.username}/>}
              {this.state.errors.global && <InlineError text="Invalid Credentials"/>}
            </div>
            
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input type="password" name="password" placeholder="password" value={this.state.data.password} onChange={this.onChange}/>
              {this.state.errors && <InlineError text={this.state.errors.password}/>}
            </div>
           
          </div>
        </div>
         <div className="form-group"><button className="btn btn-primary btn-lg">Login</button></div>
        </form>
       
      </div>

    );
  }

  
}

