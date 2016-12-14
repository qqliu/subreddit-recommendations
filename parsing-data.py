import csv
import numpy
import math
import random

userCount = 1
subredditsCount = 1
random_file_rows = 2000000

subreddits = {}
users = {}
ratingsTable = []
newRatingsTable = []
totalRatings = {}
totalPostsRatings = {}
numRatings = {}

num_comments = {}
num_posts = {}
concat_to_users = {}
combined_comments_posts = {}

boolean = False
logscale = False
gaussian = False
sum_comments_and_posts = False
compute_recall = False
normStdMean = False
numberOfRows = False
generateRandomFile = True
mu = 0.7
sigma = 0.17

computing_mean_var = False

old_files = []
bots = 'bots_botWatcher.csv'
posts_file = 'num_posts.csv'
comments_file = 'num_comments.csv'
files = ['comments_posts_max_norm_no_bots_no_default_2_3.csv', 'comments_posts_max_norm_no_bots_no_default_3_6.csv',\
        'num_posts_max_norm_no_bots_no_default.csv',\
        'num_comments_max_norm_no_bots_no_default.csv',\
        'num_posts_tfidf_max_norm_no_bots_no_default.csv',\
        'num_comments_tfidf_max_norm_no_bots_no_default.csv']

recall_file = ''
rec_file = 'avg_comment_length_max_norm_no_bots_no_default.csv'

mean_var_file = 'avg_comment_length_max_norm_no_bots_no_default.csv'

all_bots = {'TotesMessenger' : 1}

default = ['announcements','Art','AskReddit','askscience',\
'aww','blog','books','creepy','dataisbeautiful','DIY','Documentaries',\
'EarthPorn','explainlikeimfive','food','funny','Futurology','gadgets',\
'gaming','GetMotivated','gifs','history','IAmA','InternetIsBeautiful',\
'Jokes','LifeProTips','listentothis','mildlyinteresting','movies',\
'Music','news','nosleep','nottheonion','OldSchoolCool',
'personalfinance','philosophy','photoshopbattles','pics','science',\
'Showerthoughts','space','sports','television','tifu','todayilearned',\
'TwoXChromosomes','UpliftingNews', 'videos','worldnews', 'WritingPrompts']

with open(bots, 'rb') as botsFile:
    reader = csv.reader(botsFile, delimiter=',', quotechar='|')
    for r in reader:
        all_bots[r[0]] = 1

print len(all_bots)

if generateRandomFile:
    t = []
    curr_u = 1
    for i in range(random_file_rows):
        r = -10
        u = random.uniform(1, 11)
        while r < 0:
            r = int(random.gauss(mu, sigma)*100)
            r = r*1.0/100
        if u < 2:
            curr_u += 1
            t.append([curr_u, int(random.uniform(1, 20000)), r])
        else:
            t.append([curr_u, int(random.uniform(1, 20000)), r])
    with open('random_file.csv', 'wb') as f:
        writer = csv.writer(f)
        writer.writerows(t)
        
elif computing_mean_var:
    means = {}
    user_items = {}
    all_means = []
    all_diffs = []
    all_s_diffs = []
    with open(mean_var_file, 'rb') as f:
        reader = csv.reader(f, delimiter=',', quotechar='|')
        for r in reader:
            u, s, n = r
            if int(u) not in means:
                means[int(u)] = [float(n), 1]
                user_items[int(u)] = [float(n)]
            else:
                vals = means[int(u)]
                s = vals[0] + float(n)
                num_reddits = vals[1] + 1
                means[int(u)] = [s, num_reddits]
                user_items[int(u)].append(float(n))

    for k in means.keys():
        mn = means[k][0]/means[k][1]
        all_means.append(mn)
        for i in user_items[k]:
            all_diffs.append(abs(i - mn))
            all_s_diffs.append(abs(i - mn)**2)
            
    std = numpy.std(all_means)
    m = numpy.mean(all_means)
    print "Standard Div: " + str(std)
    print "Mean: " + str(m)
    print "Average Difference: " + str(numpy.mean(all_diffs))
    print "Average RMS Diff: " + str(math.sqrt(numpy.mean(all_s_diffs)))
    
elif compute_recall:
    users = {}
    with open(recall_file, 'rb') as f:
        reader = csv.reader(f, delimiter=',', quotechar='|')
        for r in reader:
            user, subreddit, num = r

            if user not in users:
                users[user] = [subreddit]
            else:
                users[user].append(subreddit)

    s = 0
    e = 0
    with open(rec_file, 'rb') as recf:
        reader = csv.reader(recf, delimiter=',', quotechar='|')
        for r in reader:
            user, subreddit = r

            if subreddit not in users[user]:
                e += 1
            s += 1

    print "Recall Error: " + e * 1.0/s
    
else:
    if numberOfRows:
        with open(rec_file, 'rb') as recFile:
            l = 0
            reader = csv.reader(recFile, delimiter=',', quotechar='|')
            for r in reader:
                l += 1
            print l
    elif sum_comments_and_posts:
        with open(posts_file, 'rb') as postsFile:
            reader = csv.reader(postsFile, delimiter=',', quotechar='|')
            for r in reader:
                user, subreddit, num = r
                
                if num != "num_posts":
                    if user+subreddit not in num_posts:
                        num_posts[user+subreddit] = 3*float(num)
                    else:
                        num_posts[user+subreddit] += 3*float(num)

                    if user not in totalPostsRatings:
                        totalPostsRatings[user] = 3*float(num)
                        numRatings[user] = 1
                    else:
                        totalPostsRatings[user] = max(3*float(num),\
                                                      totalPostsRatings[user])
                        numRatings[user] += 1

                    if user+subreddit not in concat_to_users:
                        concat_to_users[user+subreddit] = user

        with open(comments_file, 'rb') as commentsFile:
            reader = csv.reader(commentsFile, delimiter=',', quotechar='|')
            for r in reader:
                user, subreddit, coms = r

                if "num" not in coms:
                    if user+subreddit not in num_comments:
                        num_comments[user+subreddit] = float(coms)
                    else:
                        num_comments[user+subreddit] += float(coms)

                    if user not in totalRatings:
                        totalRatings[user] = float(coms)
                    else:
                        totalRatings[user] = max(float(coms), totalRatings[user])

                    if user not in numRatings:
                        numRatings[user] = 1
                    else:
                        numRatings[user] += 1

                    if user+subreddit not in concat_to_users:
                        concat_to_users[user+subreddit] = user

        for k in num_posts.keys():
            if k in num_comments:
                totalRatings[concat_to_users[k]] =  max(num_comments[k] +\
                            num_posts[k], totalRatings[concat_to_users[k]])

        for k in num_comments.keys():
            user = concat_to_users[k]
            subreddit = ''.join(k.split(user)[1:])
            if k in num_posts:
                ratings = num_comments[k] + num_posts[k]
            else:
                ratings = num_comments[k]
            ratingsTable.append([user, subreddit, ratings/totalRatings[user]])
                
        print "Done Parsing Everything"

        for k in num_posts.keys():
            if k not in num_comments:
                ratingsTable.append([concat_to_users[k],\
                                    ''.join(k.split(concat_to_users[k])[1:]),\
                                    num_posts[k]/totalPostsRatings[concat_to_users[k]]])

        print "Done Parsing Keys"

        for i in range(len(ratingsTable)):
            user, subreddit, ratings = ratingsTable[i]
            if ratings != "num_comments" and user not in all_bots and\
               subreddit not in default:
                if user not in users:
                    users[user] = userCount
                    userCount += 1
                    numRatings[users[user]] = numRatings[user]
                if subreddit not in subreddits:
                    subreddits[subreddit] = subredditsCount
                    subredditsCount += 1
                newRatingsTable.append([users[user], subreddits[subreddit], ratings])

        writingDown = []

        for b in [[2, 3], [3, 6], [15, 25]]:
                  #[15, 25], [25, 50], [50, 1000]]:
            writingDown = []
            for r in newRatingsTable:
                u = r[0]
                s = r[1]
                n = r[2]
                if n > 1:
                    print "wrong"
                if n < 0:
                    print "wrong"
                if numRatings[u] >= b[0] and numRatings[u] < b[1]:
                    writingDown.append(r)

            print len(writingDown)
            with open("comments_posts_max_norm_no_bots_no_default_" +\
                      str(b[0]) + "_" + str(b[1]) + "_weighted.csv", 'wb') as wfile:
                writer = csv.writer(wfile)
                writer.writerows(writingDown)

    elif logscale:
        for f in files:
            print f
            with open(f, 'rb') as normFile:
                reader = csv.reader(normFile, delimiter=',', quotechar='|')
                for row in reader:
                    user, subreddit, rating = row
                    # Change log by adding 1
                    ratingsTable.append([user, subreddit, math.log(max(0, float(rating)) + 1, 2)])

            with open(f.split(".")[0] + "_log.csv", 'wb') as wfile:
                writer = csv.writer(wfile)
                writer.writerows(ratingsTable)

    elif normStdMean:
        for f in files:
            print f
            
            means = {}
            user_vals = {}
            rows = []
            with open(f, 'rb') as normFile:
                reader = csv.reader(normFile, delimiter=',', quotechar = '|')
                for row in reader:
                    u, s, n = row
                    rows.append(row)
                    if int(u) not in means:
                        means[int(u)] = [float(n), 1]
                        user_vals[int(u)] = [float(n)]
                    else:
                        vals = means[int(u)]
                        s = vals[0] + float(n)
                        num_reddits = vals[1] + 1
                        means[int(u)] = [s, num_reddits]
                        user_vals[int(u)].append(float(n))
                        
            for k in means.keys():
                m = means[k]
                means[k] = [m[0]/m[1], float(numpy.std(user_vals[k]))]

            print "calculating"
            for i in range(len(rows)):
                u = int(rows[i][0])
                s = rows[i][1]
                n = rows[i][2]
                mu = means[u][0]
                std = means[u][1]
                if std > 0:
                    rows[i] = [u, s, (float(n) - mu)/std]
                else:
                    rows[i] = [u, s, float(n) - mu]

            with open(f.split(".")[0] + "_centered.csv", 'wb') as wfile:
                    writer = csv.writer(wfile)
                    writer.writerows(rows)
                
    else:
        for f in files:
            ratingsTable = []
            newRatingsTable = []
            totalRatings = {}
            numRatings = {}
            with open(f, 'rb') as csvfile:
                reader = csv.reader(csvfile, delimiter=',', quotechar='|')
                for row in reader:
                    user, subreddit, ratings = row
                    if ratings != "num_comments" and user not in all_bots and\
                       subreddit not in default:
                        if user not in users:
                            users[user] = userCount
                            userCount += 1
                        if subreddit not in subreddits:
                            subreddits[subreddit] = subredditsCount
                            subredditsCount += 1
                        if user not in totalRatings:
                            totalRatings[user] = float(ratings)
                            numRatings[user] = 1
                        else:
                            totalRatings[user] = max(float(ratings), totalRatings[user])
                            numRatings[user] += 1
                        if int(float(ratings)) != 0:
                            ratingsTable.append([user, subreddit, ratings])
                print len(ratingsTable)
                #s = [441, 1086, 827, 6435, 501, 159, 1084, 107, 456, 1467]
                #s = [2704, 637, 327, 1446, 864, 68, 5015, 101, 248, 853]
                #s = [4290, 19392, 1103, 1249, 562, 1110, 667, 412, 898, 2963]
                s = []

                names = []
                for r in range(len(ratingsTable)):
                    row = ratingsTable[r]
                    user = row[0]
                    subreddit = row[1]
                    ratings = float(row[2])
                    if totalRatings[user] != 0:
                        ratingsTable[r] = [users[user], subreddits[subreddit],\
                                    (float(ratings) * 1.0)/totalRatings[user]]

                    if boolean == True:
                        ratingsTable[r] = [users[user], subreddits[subreddit], 1]

                    if totalRatings[user] != 0 and numRatings[user] > 1 and numRatings[user] < 50:
                        newRatingsTable.append(ratingsTable[r])
                        
                    if subreddits[subreddit] in s:
                        if subreddit not in names:
                            print subreddit
                            names.append(subreddit)            
                
            print len(newRatingsTable)
                    
            with open(f.split(".")[0] + "_max_norm_no_bots_no_default.csv", 'wb') as wfile:
                writer = csv.writer(wfile)
                writer.writerows(newRatingsTable)
                       
