# General

## Introduction

### Technical version

Hi, my name is Khanh. I have about 3 year experience working as a backend developer. For previous jobs, i had to communicate with foreign co-worker in english so i believe that my english capacity can match your requirement. Regarding technical skill, I mainly work with the backend, writing resttful api with modern technique like caching, message broker, integrating 3th party. I’m also familiar with devops concepts and tools such as cicd pipeline, github action, containerization with docker and container orchetration with kubernete. For operating system, i can use mac and any kind of linux distro pretty good. Maybe in the future, i want to deep dive more about devop engineer if i had a chance

### Culture-fit version

Hi everyone, my name is Khanh, i'm a software engineer. I started going to work when i was only fresher student to apply knowledge into real-world project because I wanted to get real experience early instead of just learning theory

As you can see, i really enjoy working with tech. I usually stay late working or just research technical stuff not because I have to, but because I’m curious and always feel excited whenever i understand something deeply

I’m looking for a team where I can contribute, keep growing up and especially stay for long-term

To improve myself, i usually do leetcode problem to maintain problem-solving skill, follow the Algomaster website to pracice problems organized by patterns. I also love reading medium blog because i find that writer usually share hidden gems and whenever i discover new topics, i always try to built it by myself. For example the last thing that i build is a complete ci/cd pipeline with gitops and a purchased cloud cluster

## Project

1. Project info: info, scale, team structure
2. Responsibility và vai trò của mình trong dự án, có phải key member hay có involve lead team/ mentoring gì ko
3. Main stacks mình used
4. Include any achievements nếu có

### Ucars

UCARS is an online car marketplace based in Singapore that helps people buy and sell cars easily. To be specific, seller dont need to upload entire information of the car but just the car plate number, our platform will retrieve full information like car's details, transfer fee or road tax from singapore admin database (LTA one motoring). Buyer can search for used or new cars based on multiple field like brand, model, variant, ... then we act as an intermediate to connect to bridge the gap between dealers and buyers. Our platform also handle other concerns like coe, which stand for Certificate of Entitlement, it is a mandatory license that grants the car owner the legal right to drive a vehicle on Singapore roads because you know the government want to control vehicle population to avoid heavy traffic.

Platform not only support individual dealer but also provide management features for big dealer who want to use our platform as an official business website. They can have an isolated company with single or multiple branchs, each branch can have own users, vehicles, many more... and people with correct permission can see the activity log of members. However dealer need to purchase corresponding subscription type to enable these features

Achievements: 
- criteria builder to build a dynamic query for multiple-filter search car
- Integrate most provider service like aws s3, auth0, twilio sendgrid, sms, ... 

vehicle_make (brand) -> vehicle model (core) -> variant (feature or specification)

scraper: parf, transfer_fee, roadtax

bidding: session, record

call AI-valuation service to get price

COEs are divided into different categories based on engine capacity. A to E

vehicle_sales_agreement

### Nvidia Vietnam

ITS stand for Intelligent traffic system is an iot application. The system target for a smart city where there are many cameras around to monitor traffic in a city. So these camera will stream sensor data to our system and our system will based on pre defined algorithm to detect accident on the street like vehicle collision or a fallen tree. As soon as an accident is detected, the system will alert user, usually the government, so that they can take action right away

Mount videos through local file system to feed them into a Nvstreamer server, this server will act as real camera for local development. Now we choose video to process by add them to VST (video storage toolkit) through rest api, SDR (stream distributing & routing) will push video data to deepstream. Deepstream handle each frame and generate metadata about object tracking, then push data to kafka topic mdx-behavior. Analytic stream server subscribe to this topic to consume data, apply predefined algorithm to detect accident and push data to topic mdx-alert. ELK stack subscribe to this topic to collect data into elastic and visualize it on kibana dashboard. When fe need traffic data, they call api and backend just query from elastic to return

### CMC Global

wealth management is a digital platform that not only supports individual advisors but also large financial institutions to manage client investments through subscribtion-based feature

Currently, i'm responsible for porfolio service

Recently I'm responsible for an advisor leaderboard service. The leaderboard rank user based on kpi, which is calculated from multiple criteria like asset under management and client retention rate (activity score like number of trade). so we need to normalize these data to calculate kpi score and use redis sorted set to store.

tables: client (advisor_id, current_value), advisor, porfolio (client_id, ), 

AUM = join client with porfolio, select sum(current_value) group by advisor_id
Retention rate = active_clients / total_clients

