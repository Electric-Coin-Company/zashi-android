FROM clamav/clamav:1.4.2
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
