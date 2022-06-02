def foreman_version = '3.3'
def foreman_client_distros = [
    'el9',
    'el8',
    'el7',
    'sles12',
    'sles11'
]
def foreman_el_releases = [
    'el7',
    'el8'
]
def foreman_debian_releases = ['buster', 'bullseye', 'focal']

def pipelines_deb = [
    'install': [
        'debian10',
        'debian11',
        'ubuntu2004'
    ],
    'upgrade': [
        'debian10',
        'ubuntu2004'
    ]
]

def pipelines_el = [
    'install': [
        'centos7',
        'centos8-stream',
        'almalinux8',
    ],
    'upgrade': [
        'centos7',
        'centos8-stream',
        'almalinux8',
    ]
]

def pipelines = [
    'install': pipelines_deb['install'] + pipelines_el['install'],
    'upgrade': pipelines_deb['upgrade'] + pipelines_el['upgrade'],
]
