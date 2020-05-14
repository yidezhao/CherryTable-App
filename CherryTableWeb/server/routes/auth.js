const express =require( "express");
const User=require("../schemas/User");
const router=express.Router();
router.post("/",function(req,res){
    const {credentials} = req.body;
    User.findOne({name:credentials.username}).then(user =>{
        
        if (user){
            res.json({success:true});
        }
         else{
            console.log(credentials.username);
             res.status(400).json({errors:{global:"Invalid User"}});
         }
     })
})
module.exports = router;