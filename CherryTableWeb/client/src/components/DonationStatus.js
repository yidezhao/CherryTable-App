import React from 'react';
import '../style/DonationStatus.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import PageNavbar from './PageNavbar';
import NewRequest from './NewRequest';
import RequestIndividual from './RequestIndividual';
import { Formik, Form, Field, ErrorMessage } from 'formik'

export default class DonationStatus extends React.Component {
	constructor(props) {
    	super(props);

    	// The state maintained by this React Component. This component maintains the list of genres,
    	// and a list of movies for a specified genre.
    	this.state = {
      		id: "",
      		donation: null,
      		statusInfo: "",
      		donor:"",
      		amount:"",
      		date:""

    	}

    	this.handleIdChange = this.handleIdChange.bind(this);
    	this.submitId = this.submitId.bind(this);

	}

	handleIdChange(e) {
		this.setState({
			id: e.target.value
		});
	}

	submitId() {

		var fullUrl = window.location;
    	var baseUrl = fullUrl.protocol + '//' + fullUrl.hostname.split(':')[0] + ':8080/' + fullUrl.pathname.split('/')[0];
    	var reqUrl = baseUrl + "getDonation?id=" + this.state.id;
    	console.log(reqUrl);

    	fetch(reqUrl, {
      		method: 'GET',
    	}).then(res => {
      
      		return res.json();
    	}, err => {
      		console.log(err);
    	}).then (d => {

      	if (!d) return;
      	
      	var temp = d.info;
      	var str = temp.reduce((acc, currVal) => {
          					return acc+"\n"+currVal.stage+" at "+currVal.date.toString();
        				},"");

      	this.setState({
        	donation: d,
        	statusInfo: str,
        	donor:d.donor,
        	amount: d.amount,
        	date: d.date.toString()

      	});
      	console.log(this.state.date);

    }, err => {
      console.log(err);
    });

    
    
  }
  nextStatus() {
    var statestr = this.state.statusInfo;
    if (statestr.indexOf("Completed") !== -1 || statestr.indexOf("Delivering") !== -1) {
      return "Completed";
    } else if (statestr.indexOf("Shipped") !== -1) {
      return "Delivering";
    } else if (statestr.indexOf("Organization received") !== -1) {
      return "Shipped";
    } else if (statestr.indexOf("Donor payment confirmed") !== -1) {
      return "Organization received";
    } else {
      return "Info Lost";
    }
  }

	
  	

    update(){
    console.log("hi")
    var fullUrl = window.location;
    var baseUrl = fullUrl.protocol + '//' + fullUrl.hostname.split(':')[0] + ':8080/' + fullUrl.pathname.split('/')[0];
    var reqUrl = baseUrl + 'updateStatus?' +
      'id=' + this.state.id +
      '&status=' + this.nextStatus();
      
    console.log(reqUrl);

    fetch(reqUrl, {
      method: 'GET',
    }).then(response => {
      if (response.ok) {
        response.json()
        .then(json => {
          console.log(json);
          alert(JSON.stringify(json, null, 2));
        });
      } else {
        alert('Failed to update new status.');
      }
    })
	}

    render() {
    	if (!this.state.donation) {
    		return (
    			
    		<div className="DonationStatus">

        		<PageNavbar active="DonationStatus" />

        		<div className="container DonationStatus-containter">

        			<div className="h5">Lookup donation</div>
			    	<div className="input-container">
			    			<input type='text' placeholder="Enter Donation id" value={this.state.id} onChange={this.handleIdChange} className="login-input"/>
							
							<button id="submitIdBtn" className="submit-btn" onClick={this.submitId}>Submit</button>
			    	</div>

			    	<div className='jumbotron'>
			    	</div>
			    </div>
			</div>);
    	} else {
    	return (
    		<div className="DonationStatus">

        		<PageNavbar active="DonationStatus" />

        		<div className="container DonationStatus-containter">

        			<div className="h5">Lookup donation</div>
			    	<div className="input-container">
			    			<input type='text' placeholder="Enter Donation id" value={this.state.id} onChange={this.handleIdChange} className="login-input"/>
							
							<button id="submitIdBtn" className="submit-btn" onClick={this.submitId}>Submit</button>
			    	</div>

			    	<div className='jumbotron'>
          			<Formik
            			initialValues={{ 
            				isInitialValid: false, 
            				id: this.state.id,
            				 date: this.state.date,
            				donor: this.state.donor,
            				
            				amount:this.state.amount,
              				status:this.state.statusInfo
            				
            	
            			}}
            			validate={() => ({})}
            			onSubmit={(values, { setSubmitting }) => {

              				this.update();
              				setSubmitting(false);
              				
            			}}
          >
            {({isValid, isSubmitting }) => (
              <Form>
                <h3> Details </h3>
                <h5> id </h5>
                <Field label='id' type='id' name='id' disabled={true}/>
                <h5> Donor </h5>
                <Field label='donor' type='donor' name='donor' disabled={true}/>
                <h5> Date </h5>
                <Field label='date' type='donor' name='date' disabled={true}/>
                <h5> Amount </h5>
                <Field label='amount' type='amount' name='amount' disabled={true}/>
                <h5> Status </h5>
                <Field label='status' component='textarea' type='status' name='status' disabled={true}/>
                
                <button type='submit' className='btn btn-primary' disabled={!isValid || isSubmitting}>
                  Update status: {this.nextStatus()}
                </button>
                <button type='cancel' className='btn btn-danger' onClick={() => {this.toggle()}}>
                  Back
                </button>
              </Form>
            )}
          </Formik>
        </div>
			    		
			    </div>
       
          
       
      		</div>

    	)
    }
}
}
