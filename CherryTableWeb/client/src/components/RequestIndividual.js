import React from 'react';
import '../style/RequestIndividual.css';
import { Formik, Field, Form, ErrorMessage } from 'formik'
import 'bootstrap/dist/css/bootstrap.min.css'

export default class RequestIndividual extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			expand: this.props.expand
      
		}
	}

	

	toggle() {
		this.setState({
			expand: !this.state.expand
		})
	}

	submit(values){
    console.log("hi")
    var fullUrl = window.location;
    var baseUrl = fullUrl.protocol + '//' + fullUrl.hostname.split(':')[0] + ':8080/' + fullUrl.pathname.split('/')[0];
    var reqUrl = baseUrl + 'updateRequest?' +
      'id=' + this.props.id +
      '&desc=' + values.description;
      
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
		if (!this.state.expand) {
			return (
				<div className="request">
					<div className="title">
						{this.props.title} 
						<button onClick={() => {this.toggle()}}>Details </button>
        	</div> 
          <div className="time">{this.props.target} </div>
					<div className="amount">{this.props.fulfilled}</div>
				</div>
			)
		} else {
			return (
				<div className='jumbotron'>
          <Formik
            initialValues={{ 
            	isInitialValid: true, 
            	id: this.props.id,
            	title: this.props.title, 
            	target:this.props.target,
              fulfilled:this.props.fulfilled,
            	description:this.props.description,
            	donations:this.props.donations
            	
            }}
            validate={() => ({})}
            onSubmit={(values, { setSubmitting }) => {
              this.submit(values);
              setSubmitting(false);
              this.toggle();
            }}
          >
            {({isValid, isSubmitting }) => (
              <Form>
                <h3> Details </h3>
                <h5> id </h5>
                <Field label='id' type='id' name='id' disabled={true}/>
                <h5> Title </h5>
                <Field label='title' type='title' name='title' disabled={true}/>
                <h5> Target </h5>
                <Field label='target' type='target' name='target' disabled={true}/>
                <h5> Fulfilled </h5>
                <Field label='fulfilled' type='fulfilled' name='fulfilled' disabled={true}/>
                <h5> Description </h5>
                <Field label='description' component='textarea' type='description' name='description' />
                <h5> List of donation IDs </h5>
                <Field label='donations' component='textarea' type='description' name='donations' disabled={true}/>
                <ErrorMessage name='description' component='div' />
                <button type='submit' className='btn btn-primary' disabled={!isValid || isSubmitting}>
                  Update description
                </button>
                <button type='cancel' className='btn btn-danger' onClick={() => {this.toggle()}}>
                  Back
                </button>
              </Form>
            )}
          </Formik>
        </div>
			)
		}
		

	}
}
