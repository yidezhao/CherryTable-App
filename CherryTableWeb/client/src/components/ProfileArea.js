import React from 'react'
import '../style/ProfileArea.css';
import { Formik, Form, Field, ErrorMessage } from 'formik'
import 'bootstrap/dist/css/bootstrap.min.css'
// import 'react-dropdown/style.css';
import cookie from 'react-cookies'

export default class ProfileArea extends React.Component {
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

  submit(values, callback) {
    let oldName = cookie.load('username');

    var reqUrl = 'http://localhost:8080/updateOrgProfile?' +
    'name=' + encodeURIComponent(oldName) +
      '&oldName=' + encodeURIComponent(oldName) +
      '&email=' + encodeURIComponent(values.email) +
      '&phone=' + encodeURIComponent(values.phone) +
      '&address=' + encodeURIComponent(values.address) +
      '&category=' + encodeURIComponent(values.category) +
      '&description=' + encodeURIComponent(values.desc.trim());
    console.log(reqUrl);
    
    fetch(reqUrl).then(res => console.log(res.body))
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        var res = JSON.parse(this.responseText);
        if (res.status == 'That organization name is taken!') {alert('That organization name is taken!');}
        else {
          cookie.save('username', values.name);
        }
      }
    }
    xmlhttp.open('GET', reqUrl, true);
    xmlhttp.send();
  }

  render() {
    if (!this.state.expand) {
      return (
        <div className='big_infobox'>
        <button type='button'
          className='btn btn-primary'
          onClick={() => {this.toggle()}}>Edit Organization Details</button>
        </div>
      )
    } else {
      return (
        <div className='jumbotron'>
          <Formik
            initialValues={{ isInitialValid: false }}
            validate={values => {

              const errors = {};

              if (!values.email) {
                  errors.email = 'Required'
              }

              if (!values.phone) {
                errors.phone = 'Required'
              }

              if (!values.address) {
                errors.address = 'Required'
              }

              if (!/^\d{10}$/i.test(values.phone)) {
                  errors.phone = 'Invalid phone number'
              }

              if (!/\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/i.test(values.email)) {
                errors.email = 'Invalid email address'
              }

              return errors;
            }}
            onSubmit={(values, { setSubmitting }) => {
              this.submit(values);
              setTimeout(() => {
                // alert(JSON.stringify(values, null, 2));
                setSubmitting(false);
                this.toggle();
              }, 400);
            }}
          >
            {({isValid, isSubmitting }) => (
              <Form>
                <h2> Edit Organization Details</h2>

                <h4> Organization Name </h4>
                <Field label='name' type='name' name='name' placeholder={cookie.load('username')} disabled={true}/>
                <ErrorMessage name='name' component='div' />

                <h4> Email Address </h4>
                <Field label='email' type='email' name='email' placeholder='donate@organization.com' />
                <ErrorMessage name='email' component='div' />

                <h4> Phone Number </h4>
                <Field label='phone' type='phone' name='phone' placeholder='215555555' />
                <ErrorMessage name='phone' component='div' />

                <h4> Physical Address </h4>
                <Field label='address' type='address' name='address' placeholder='3330 Walnut St' />

                <h4> Category </h4>
                <Field label='category' type='category' name='category' placeholder='Disaster Relief, Hunger, etc.' />

                <h4> Description </h4>
                <Field label='description' type='desc' name='desc' component='textarea' placeholder='What does your organization do? ... ' />
                
                <br></br>
                <button type='submit' className='btn btn-primary' disabled={!isValid || isSubmitting}>
                  Save
                </button>
                <button type='cancel' className='btn btn-danger' onClick={() => {this.toggle()}}>
                  Cancel
                </button>
              </Form>
            )}
          </Formik>
        </div>
      )
    }
  }
}
