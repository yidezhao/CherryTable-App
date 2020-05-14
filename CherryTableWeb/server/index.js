const path =require("path");
const bodyParser = require('body-parser');
const express = require('express');
var routes = require("./routes.js");
const cors = require('cors');
const auth = require('./routes/auth')
const User=require("./schemas/User");
const app = express();
const port = 8080;


app.use(cors({ credentials: true, origin: 'http://localhost:3000' }));
app.use(bodyParser.json());

app.use(bodyParser.urlencoded({extended: false}));

app.post('/api/auth',(req,res)=>{
const {credentials} = req.body;
User.findOne({name:credentials.username}).then(user =>{
    
    if (credentials.username=='test' && credentials.password=='12345'){
        res.json({success:true});
    }
     else{
        console.log(credentials.username);
         res.status(400).json({errors:{global:"Invalid User"}});
     }
 });});
    

app.get('/createdummy', routes.createDummy);
app.get('/createdummy2', routes.createDummy2);
app.get('/newUser', routes.newUser);
app.get('/newRequest', routes.newRequest);
app.get('/organizationsNearLocation', routes.organizationsNearLocation)
app.get('/home', (req, res) => res.send('Hello World!'));
app.get('/getCurrentProfile', routes.getCurrentProfile);

app.post('/updateUserProfile', routes.updateUserProfile);
app.post('/uploadImageMobileUser', routes.uploadImageMobileUser);

app.post('/deleteProfile', routes.deleteProfile);

app.get('/fetchOrgDetails', routes.fetchOrgDetails);
app.get('/updateOrgProfile', routes.updateOrgProfile);
app.get('/fetchDonorsForOrg', routes.fetchDonorsForOrg);
app.post('/uploadImage', routes.uploadImage);

app.post('/uploadImageGallery', routes.uploadImageGallery);

app.get('/requests', routes.getAllRequests);
app.get('/updateRequest', routes.updateRequest);
app.get('/getDonation', routes.getDonation);
app.get('/updateStatus', routes.updateStatus);

app.get('/getAllDonations', routes.getAllDonations);
app.get('/makeDonation', routes.makeDonation);

app.listen(port, () => console.log(`CherryTable listening at http://localhost:${port}`));
